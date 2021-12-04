package org.apache.spark.ml.made

import breeze.linalg.{ sum, DenseVector => BVector }
import org.apache.spark.ml.PredictorParams
import org.apache.spark.ml.linalg.{ DenseVector, Vector, Vectors }
import org.apache.spark.ml.param.{ DoubleParam, IntParam, ParamMap }
import org.apache.spark.ml.regression.{ RegressionModel, Regressor }
import org.apache.spark.ml.stat.Summarizer
import org.apache.spark.ml.util._
import org.apache.spark.sql.catalyst.encoders.ExpressionEncoder
import org.apache.spark.sql.{ DataFrame, Dataset, Encoder, Row }

trait LinearRegressionParams extends PredictorParams {

  final val numberIterations: IntParam = new IntParam(this, "numberIterations", "Iterations number")
  final val learningRate: DoubleParam  = new DoubleParam(this, "learningRate", "Learning Rate")

  setDefault(learningRate, 1.0)
  setDefault(numberIterations, 100)

  def setLearningRate(value: Double): this.type = set(learningRate, value)

  def setNumberIterations(value: Int): this.type = set(numberIterations, value)
}

class LinearRegression(override val uid: String)
    extends Regressor[Vector, LinearRegression, LinearRegressionModel]
    with LinearRegressionParams
    with DefaultParamsWritable {

  def this() = this(Identifiable.randomUID("linearRegression"))

  override def copy(extra: ParamMap): LinearRegression = defaultCopy(extra)

  override protected def train(dataset: Dataset[_]): LinearRegressionModel = {
    val numFeatures              = MetadataUtils.getNumFeatures(dataset, $(featuresCol))
    var weights: BVector[Double] = BVector.zeros(numFeatures + 1)
    val gradCol                  = "grad"

    val transformUdf = dataset
      .sqlContext
      .udf
      .register(
        uid + "_grad",
        (x_no_ones: Vector, y: Double) => {
          val one  = BVector(1.0)
          val x    = BVector.vertcat(one, x_no_ones.asBreeze.toDenseVector)
          val grad = x * (sum(x * weights) - y)
          Vectors.fromBreeze(grad)
        }
      )

    for (_ <- 0 to $(numberIterations)) {
      val transformed_dataset     = dataset.withColumn(gradCol, transformUdf(dataset($(featuresCol)), dataset($(labelCol))))
      val Row(Row(grad_mean_arr)) = transformed_dataset
        .select(Summarizer.metrics("mean").summary(transformed_dataset(gradCol)))
        .first()

      val grad_mean: BVector[Double] = grad_mean_arr.asInstanceOf[DenseVector].asBreeze.toDenseVector
      weights = weights - $(learningRate) * grad_mean
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
    val one = BVector(1.0)
    val x   = BVector.vertcat(one, features.asBreeze.toDenseVector)
    sum(x * weights.asBreeze.toDenseVector)
  }

  override def copy(extra: ParamMap): LinearRegressionModel = copyValues(new LinearRegressionModel(weights))

  def getWeights(): BVector[Double] =
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
