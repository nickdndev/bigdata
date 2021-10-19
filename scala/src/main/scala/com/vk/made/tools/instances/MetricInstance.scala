package com.vk.made.tools.instances

import breeze.linalg.DenseVector
import breeze.numerics.pow
import breeze.stats.mean

private[tools] trait MetricInstance {
  implicit class MetricExt(val data: (DenseVector[Double], DenseVector[Double])) {

    def mse: Double =
      mean(pow(data._1 - data._2, 2))
  }
}
