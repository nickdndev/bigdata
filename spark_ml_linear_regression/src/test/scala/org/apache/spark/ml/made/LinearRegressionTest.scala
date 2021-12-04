package org.apache.spark.ml.made

import breeze.linalg.DenseVector
import com.google.common.io.Files
import org.apache.spark.ml.evaluation.RegressionEvaluator
import org.apache.spark.ml.{ Pipeline, PipelineModel }
import org.apache.spark.sql.DataFrame
import org.scalatest.flatspec._
import org.scalatest.matchers._

class LinearRegressionTest extends AnyFlatSpec with should.Matchers with WithSpark with WithData {

  private def validateModel(model: LinearRegressionModel, data: DataFrame): Unit = {
    val df_result = model.transform(df)

    val evaluator = new RegressionEvaluator()
      .setLabelCol("label")
      .setPredictionCol("prediction")
      .setMetricName("mse")

    val mse = evaluator.evaluate(df_result)
    mse should be < 0.015
  }

  val precisionDelta = 0.01

  "Estimator" should "model weights" in {
    val lr = new LinearRegression()
      .setFeaturesCol("features")
      .setLabelCol("label")
      .setPredictionCol("prediction")
      .setLearningRate(1.0)
      .setNumberIterations(100)

    val model                       = lr.fit(df)
    val params: DenseVector[Double] = model.getWeights()

    params(0) should be(0.022 +- precisionDelta)
    params(1) should be(0.0016 +- precisionDelta)
    params(2) should be(-0.14 +- precisionDelta)
    params(3) should be(0.86 +- precisionDelta)
    params(4) should be(-0.06 +- precisionDelta)
  }

  "Model" should "estimation (mse) " in {
    val lr = new LinearRegression()
      .setFeaturesCol("features")
      .setLabelCol("label")
      .setPredictionCol("prediction")
      .setLearningRate(1.0)
      .setNumberIterations(100)

    val model = lr.fit(df)
    validateModel(model, df)
  }

  "Estimator" should "work after re-read" in {

    val pipeline = new Pipeline().setStages(
      Array(
        new LinearRegression()
          .setFeaturesCol("features")
          .setLabelCol("label")
          .setPredictionCol("prediction")
          .setLearningRate(1.0)
          .setNumberIterations(100)
      )
    )

    val tmpFolder = Files.createTempDir()

    pipeline.write.overwrite().save(tmpFolder.getAbsolutePath)

    val model = Pipeline
      .load(tmpFolder.getAbsolutePath)
      .fit(df)
      .stages(0)
      .asInstanceOf[LinearRegressionModel]

    validateModel(model, df)
  }

  "Model" should "work after re-read" in {

    val pipeline = new Pipeline().setStages(
      Array(
        new LinearRegression()
          .setFeaturesCol("features")
          .setLabelCol("label")
          .setPredictionCol("prediction")
          .setLearningRate(1.0)
          .setNumberIterations(100)
      )
    )

    val model     = pipeline.fit(df)
    val tmpFolder = Files.createTempDir()
    model.write.overwrite().save(tmpFolder.getAbsolutePath)

    val reRead = PipelineModel.load(tmpFolder.getAbsolutePath)

    validateModel(reRead.stages(0).asInstanceOf[LinearRegressionModel], df)
  }
}
