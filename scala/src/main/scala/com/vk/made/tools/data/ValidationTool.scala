package com.vk.made.tools.data

import breeze.linalg.DenseVector
import breeze.numerics.pow
import breeze.stats.mean

object ValidationTool {
  def MSE(y_predict: DenseVector[Double], y_true: DenseVector[Double]): Double =
    mean(pow(y_predict - y_true, 2))
}
