package com.vk.made.models

import breeze.linalg.{ DenseMatrix, DenseVector }
import cats.implicits.catsSyntaxOptionId

class LinearRegression {
  var weightsOpt: Option[DenseVector[Double]] = None

  def fit(X: DenseMatrix[Double], y: DenseVector[Double]): Unit = {
    val cov    = (DenseMatrix.zeros[Double](X.cols, X.cols) + (X.t * X))
    val scaled = DenseVector.zeros[Double](X.cols) + (X.t * y)
    weightsOpt = (cov \ scaled).some
  }

  def predict(X: DenseMatrix[Double]): DenseVector[Double] =
    weightsOpt match {
      case None          => throw new RuntimeException("Model is not fitted,please fit model before call predict!!")
      case Some(weights) => X * weights
    }

}

object LinearRegression {
  def apply(): LinearRegression = {
    val linearRegression = new LinearRegression()
    linearRegression
  }
}
