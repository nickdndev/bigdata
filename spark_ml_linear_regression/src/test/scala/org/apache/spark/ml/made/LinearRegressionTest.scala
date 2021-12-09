package org.apache.spark.ml.made
import breeze.linalg.DenseVector
import com.google.common.io.Files
import org.apache.spark.ml.evaluation.RegressionEvaluator
import org.apache.spark.ml.{ Pipeline, PipelineModel }
import org.apache.spark.sql.DataFrame
import org.scalatest.flatspec._
import org.scalatest.matchers._

class LinearRegressionTest extends AnyFlatSpec with should.Matchers with WithSpark with WithData {

  private def check(model: LinearRegressionModel, data: DataFrame): Unit = {
    val mse = new RegressionEvaluator()
      .setLabelCol("label")
      .setPredictionCol("prediction")
      .setMetricName("mse")
      .evaluate(model.transform(df))

    mse should be < 0.015
  }

  val precisionDelta = 0.05

  "Estimator" should "model weights" in {

    val hiddenModel = Array(1.5, 0.3, -0.7)
    val lr          = new LinearRegression()
      .setFeaturesCol("features")
      .setLabelCol("label")
      .setPredictionCol("prediction")
      .setLearningRate(0.8)
      .setNumberIterations(2)
      .setBatchSize(10000)

    val model                       = lr.fit(df)
    val params: DenseVector[Double] = model.getWeights()

    params(1) should be(hiddenModel(0) +- precisionDelta)
    params(2) should be(hiddenModel(1) +- precisionDelta)
    params(3) should be(hiddenModel(2) +- precisionDelta)
  }

  "Model" should "estimation (mse) " in {
    val lr = new LinearRegression()
      .setFeaturesCol("features")
      .setLabelCol("label")
      .setPredictionCol("prediction")
      .setLearningRate(0.8)
      .setNumberIterations(2)
      .setBatchSize(10000)

    val model = lr.fit(df)
    check(model, df)
  }

  "Estimator" should "work after re-read" in {

    val pipeline = new Pipeline().setStages(
      Array(
        new LinearRegression()
          .setFeaturesCol("features")
          .setLabelCol("label")
          .setPredictionCol("prediction")
          .setLearningRate(0.8)
          .setNumberIterations(2)
          .setBatchSize(10000)
      )
    )

    val tmpFolder = Files.createTempDir()

    pipeline.write.overwrite().save(tmpFolder.getAbsolutePath)

    val model = Pipeline
      .load(tmpFolder.getAbsolutePath)
      .fit(df)
      .stages(0)
      .asInstanceOf[LinearRegressionModel]

    check(model, df)
  }

  "Model" should "work after re-read" in {

    val pipeline = new Pipeline().setStages(
      Array(
        new LinearRegression()
          .setFeaturesCol("features")
          .setLabelCol("label")
          .setPredictionCol("prediction")
          .setLearningRate(0.8)
          .setNumberIterations(2)
          .setBatchSize(10000)
      )
    )

    val model     = pipeline.fit(df)
    val tmpFolder = Files.createTempDir()
    model.write.overwrite().save(tmpFolder.getAbsolutePath)

    val reRead = PipelineModel.load(tmpFolder.getAbsolutePath)

    check(reRead.stages(0).asInstanceOf[LinearRegressionModel], df)
  }
}
