package org.apache.spark.ml.made

import breeze.linalg.{ sum, DenseVector => BreezeVector }
import org.apache.spark.ml.PredictorParams
import org.apache.spark.ml.linalg.{ DenseVector, Vector, Vectors }
import org.apache.spark.ml.param.{ DoubleParam, IntParam, ParamMap }
import org.apache.spark.ml.regression.{ RegressionModel, Regressor }
import org.apache.spark.ml.stat.Summarizer
import org.apache.spark.ml.util._
import org.apache.spark.sql.catalyst.encoders.ExpressionEncoder
import org.apache.spark.sql.functions.rand
import org.apache.spark.sql.{ DataFrame, Dataset, Encoder, Row }

trait LinearRegressionParams extends PredictorParams {

  val numberIterations: IntParam = new IntParam(this, "numberIterations", "Iterations number")
  val learningRate: DoubleParam  = new DoubleParam(this, "learningRate", "Learning Rate")
  val batchSize: IntParam        = new IntParam(this, "batchSize", "Batch Size")

  setDefault(learningRate, 0.01)
  setDefault(numberIterations, 50)
  setDefault(batchSize, 1024)

  def setLearningRate(value: Double): this.type = set(learningRate, value)

  def setNumberIterations(value: Int): this.type = set(numberIterations, value)

  def setBatchSize(value: Int): this.type = set(batchSize, value)

}

class LinearRegression(weightsOpt: Option[BreezeVector[Double]], override val uid: String)
    extends Regressor[Vector, LinearRegression, LinearRegressionModel]
    with LinearRegressionParams
    with DefaultParamsWritable {

  def this(uid: String) {
    this(None, uid)
  }
  def this() {
    this(None, Identifiable.randomUID("linearRegression"))
  }
  def this(weights: BreezeVector[Double]) {
    this(Some(weights), Identifiable.randomUID("linearRegression"))
  }

  override def copy(extra: ParamMap): LinearRegression = defaultCopy(extra)

  override protected def train(dataset: Dataset[_]): LinearRegressionModel = {
    val numberFeatures: Int           = MetadataUtils.getNumFeatures(dataset, $(featuresCol))
    var weights: BreezeVector[Double] = weightsOpt.getOrElse(BreezeVector.zeros(numberFeatures + 1))
    val gradientColumnName            = "gradient"

    val gradientEstimation = dataset
      .sqlContext
      .udf
      .register(
        name = uid + "_gradient",
        func = { (features: Vector, y: Double) =>
          val x    = BreezeVector.vertcat(BreezeVector(1.0), features.asBreeze.toDenseVector)
          val grad = x * (sum(x * weights) - y)
          Vectors.fromBreeze(grad)
        }
      )

    for (_ <- 0 to $(numberIterations)) {
      val transformedDataset: DataFrame = dataset
        .orderBy(rand())
        .limit($(batchSize))
        .withColumn(gradientColumnName, gradientEstimation(dataset($(featuresCol)), dataset($(labelCol))))

      val meanGradient = transformedDataset
        .select(
          Summarizer
            .metrics("mean")
            .summary(transformedDataset(gradientColumnName))
        )
        .first()

      meanGradient match {
        case Row(Row(meanGradient)) =>
          weights = weights - $(learningRate) * meanGradient.asInstanceOf[DenseVector].asBreeze.toDenseVector
      }
    }
    val params = Vectors.fromBreeze(weights)

    copyValues(new LinearRegressionModel(params)).setParent(this)
  }

}

object LinearRegression extends DefaultParamsReadable[LinearRegression]

class LinearRegressionModel protected[made] (override val uid: String, weights: Vector)
    extends RegressionModel[Vector, LinearRegressionModel]
    with PredictorParams
    with MLWritable {

  def this(weights: Vector) = this(Identifiable.randomUID("linearRegressionModel"), weights)

  override def predict(features: Vector): Double = {
    val x = BreezeVector.vertcat(BreezeVector(1.0), features.asBreeze.toDenseVector)
    sum(x * weights.asBreeze.toDenseVector)
  }

  override def copy(extra: ParamMap): LinearRegressionModel = copyValues(new LinearRegressionModel(weights))

  def getWeights(): BreezeVector[Double] =
    weights.asBreeze.toDenseVector

  override def write: MLWriter =
    new DefaultParamsWriter(this) {
      override protected def saveImpl(path: String): Unit = {
        super.saveImpl(path)

        val params = Tuple1(weights.asInstanceOf[Vector])

        sqlContext.createDataFrame(Seq(params)).write.parquet(path + "/vectors")
      }
    }
}

object LinearRegressionModel extends MLReadable[LinearRegressionModel] {
  override def read: MLReader[LinearRegressionModel] =
    new MLReader[LinearRegressionModel] {
      override def load(path: String): LinearRegressionModel = {
        val metadata = DefaultParamsReader.loadMetadata(path, sc)

        val vectors: DataFrame = sqlContext.read.parquet(path + "/vectors")

        implicit val encoder: Encoder[Vector] = ExpressionEncoder()

        val (params: Vector) = vectors.select(vectors("_1").as[Vector]).first()

        val model = new LinearRegressionModel(params)
        metadata.getAndSetParams(model)
        model
      }
    }
}
