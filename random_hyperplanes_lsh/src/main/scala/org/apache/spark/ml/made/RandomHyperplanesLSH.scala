package org.apache.spark.ml.made

import org.apache.hadoop.fs.Path
import org.apache.spark.ml.feature.{ LSH, LSHModel }
import org.apache.spark.ml.linalg
import org.apache.spark.ml.linalg.{ Matrices, Matrix, Vector, VectorUDT, Vectors }
import org.apache.spark.ml.param.ParamMap
import org.apache.spark.ml.param.shared.{ HasInputCol, HasOutputCol }
import org.apache.spark.ml.util._
import org.apache.spark.mllib.util.MLUtils
import org.apache.spark.sql.Row
import org.apache.spark.sql.types.StructType

import scala.util.Random

trait RandomHyperplanesLSHParams extends HasInputCol with HasOutputCol {
  def setInputCol(value: String): this.type  = set(inputCol, value)
  def setOutputCol(value: String): this.type = set(outputCol, value)

  protected def validateAndTransformSchema(schema: StructType): StructType = {
    SchemaUtils.checkColumnType(schema, getInputCol, new VectorUDT())

    if (schema.fieldNames.contains($(outputCol))) {
      SchemaUtils.checkColumnType(schema, getOutputCol, new VectorUDT())
      schema
    } else
      SchemaUtils.appendColumn(schema, schema(getInputCol).copy(name = getOutputCol))
  }
}

class RandomHyperplanesLSH(override val uid: String) extends LSH[RandomHyperplanesLSHModel] {

  override def setInputCol(value: String): this.type = super.setInputCol(value)

  override def setOutputCol(value: String): this.type = super.setOutputCol(value)

  override def setNumHashTables(value: Int): this.type = super.setNumHashTables(value)

  def this() = {
    this(Identifiable.randomUID("randomHyperplanesLSH"))
  }

  override protected[this] def createRawLSHModel(inputDim: Int): RandomHyperplanesLSHModel = {
    val rand                           = new Random(0)
    val randHyperPlanes: Array[Vector] =
      Array.fill($(numHashTables)) {
        val randArray = Array.fill(inputDim)(if (rand.nextGaussian() > 0) 1.0 else -1.0)
        linalg.Vectors.fromBreeze(breeze.linalg.Vector(randArray))
      }
    new RandomHyperplanesLSHModel(uid, randHyperPlanes)
  }

  override def copy(extra: ParamMap): this.type = defaultCopy(extra)

  override def transformSchema(schema: StructType): StructType = {
    SchemaUtils.checkColumnType(schema, $(inputCol), new VectorUDT)
    validateAndTransformSchema(schema)
  }
}

class RandomHyperplanesLSHModel private[made] (
    override val uid: String,
    private[made] val randHyperPlanes: Array[Vector]
) extends LSHModel[RandomHyperplanesLSHModel] {

  override def setInputCol(value: String): this.type = super.set(inputCol, value)

  override def setOutputCol(value: String): this.type = super.set(outputCol, value)

  private[made] def this(randHyperPlanes: Array[Vector]) =
    this(Identifiable.randomUID("randomHyperplanesLSH"), randHyperPlanes)

  override protected[ml] def hashFunction(elems: linalg.Vector): Array[linalg.Vector] =
    randHyperPlanes.map { randHyperPlane =>
      if (elems.dot(randHyperPlane) >= 0)
        1
      else
        -1
    }.map(Vectors.dense(_))

  override protected[ml] def keyDistance(x: linalg.Vector, y: linalg.Vector): Double =
    if (Vectors.norm(x, 2) == 0 || Vectors.norm(y, 2) == 0)
      1.0
    else
      1.0 - x.dot(y) / (Vectors.norm(x, 2) * Vectors.norm(y, 2))

  override protected[ml] def hashDistance(x: Seq[linalg.Vector], y: Seq[linalg.Vector]): Double =
    x.zip(y)
      .map {
        case (left, right) if left == right => 1
        case _                              => 0
      }
      .sum
      .toDouble / x.size

  override def write: MLWriter =
    new RandomHyperplanesLSHModel.RandomHyperplanesLSHModelWriter(this)

  override def copy(extra: ParamMap): RandomHyperplanesLSHModel = {
    val copied = new RandomHyperplanesLSHModel(uid, randHyperPlanes).setParent(parent)
    copyValues(copied, extra)
  }

}

object RandomHyperplanesLSHModel extends MLReadable[RandomHyperplanesLSHModel] {
  override def read: MLReader[RandomHyperplanesLSHModel] =
    new MLReader[RandomHyperplanesLSHModel] {

      private val className = classOf[RandomHyperplanesLSHModel].getName
      override def load(path: String): RandomHyperplanesLSHModel = {
        val metadata = DefaultParamsReader.loadMetadata(path, sc, className)

        val dataPath                     = new Path(path, "data").toString
        val data                         = sparkSession.read.parquet(dataPath)
        val Row(randHyperPlanes: Matrix) = MLUtils
          .convertMatrixColumnsToML(data, "randHyperPlanes")
          .select("randHyperPlanes")
          .head()
        val model                        = new RandomHyperplanesLSHModel(metadata.uid, randHyperPlanes.rowIter.toArray)

        metadata.getAndSetParams(model)
        model
      }
    }

  override def load(path: String): RandomHyperplanesLSHModel = super.load(path)

  private[RandomHyperplanesLSHModel] class RandomHyperplanesLSHModelWriter(instance: RandomHyperplanesLSHModel) extends MLWriter {

    private case class Data(randHyperPlanes: Matrix)

    override protected def saveImpl(path: String): Unit = {
      DefaultParamsWriter.saveMetadata(instance, path, sc)
      val numRows    = instance.randHyperPlanes.length
      require(numRows > 0)
      val numCols    = instance.randHyperPlanes.head.size
      val values     = instance.randHyperPlanes.map(_.toArray).reduce(Array.concat(_, _))
      val randMatrix = Matrices.dense(numRows, numCols, values)
      val data       = Data(randMatrix)
      val dataPath   = new Path(path, "data").toString
      sparkSession.createDataFrame(Seq(data)).repartition(1).write.parquet(dataPath)
    }
  }

}
