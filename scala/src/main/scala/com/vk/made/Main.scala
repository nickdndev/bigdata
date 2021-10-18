package com.vk.made

import com.vk.made.models.LinearRegression
import com.vk.made.tools.data.DataTool.{readCsv, writeCsv}
import com.vk.made.tools.data.ValidationTool.MSE

object Main extends App {

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
  val (x_train, y_train) = readCsv(filePath = trainInput, targetColumn = targetColumn)

  val linearRegressionModel: LinearRegression = LinearRegression()
  linearRegressionModel.fit(x_train, y_train)

  val y_train_predict = linearRegressionModel.predict(x_train)
  val mse_train       = MSE(y_train_predict, y_train)
  writeCsv(trainOutput, y_train_predict)

  val (x_val, y_val) = readCsv(filePath = valInput, targetColumn = targetColumn)
  val y_val_predict  = linearRegressionModel.predict(x_val)
  val mse_val        = MSE(y_val_predict, y_val)
  writeCsv(valOutput, y_val_predict)
}
