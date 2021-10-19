package com.vk.made

import breeze.linalg.DenseVector
import com.vk.made.models.LinearRegression
import com.vk.made.tools._
import org.slf4j.LoggerFactory
object Main extends App {
  val logger = LoggerFactory.getLogger(getClass.getSimpleName)

  var targetColumn: Int = 0

  var trainInput: String = ""
  var valInput: String   = ""

  var trainOutput: String = ""
  var valOutput: String   = ""

  args
    .sliding(2, 2)
    .toList
    .foreach {
      case Array("--target", arg: String)            => targetColumn = arg.toInt
      case Array("--train", arg: String)             => trainInput = arg
      case Array("--validation", arg: String)        => valInput = arg
      case Array("--train-output", arg: String)      => trainOutput = arg
      case Array("--validation-output", arg: String) => valOutput = arg
    }
  logger.info(s"""
                 | Target column: $targetColumn
                 | Train: $trainInput
                 | Val:$valInput
                 | Train output: $trainOutput
                 | Val output: $valOutput 
                 | """.stripMargin)

  val (x_train, y_train) = trainInput.fromFile(targetColumn = targetColumn)

  val linearRegressionModel: LinearRegression = LinearRegression()

  logger.info("Model is starting fit")

  linearRegressionModel.fit(x_train, y_train)

  logger.info("Train model finished")

  val y_train_predict: DenseVector[Double] = linearRegressionModel.predict(x_train)
  val mse_train                            = (y_train_predict, y_train).mse

  logger.info(s"MSE model for train dataset is:$mse_train")

  y_train_predict.toFile(trainOutput)

  val (x_val, y_val) = valInput.fromFile(targetColumn = targetColumn)
  val y_val_predict  = linearRegressionModel.predict(x_val)
  val mse_val        = (y_val_predict, y_val).mse

  logger.info(s"MSE model for val dataset is:$mse_val")

  y_val_predict.toFile(valOutput)

}
