package org.apache.spark.ml.made

import breeze.linalg.{ DenseMatrix, DenseVector }
import breeze.stats.distributions.Rand
import org.apache.spark.ml.linalg.{ Matrices, Vector, Vectors }
import org.apache.spark.sql.DataFrame

trait WithData extends WithSpark {
  import spark.implicits._

  val featuresMatrix: DenseMatrix[Double] = DenseMatrix.rand(100000, 3, Rand.gaussian)
  val hiddenModel: DenseVector[Double]    = DenseVector(Array(1.5, 0.3, -0.7))
  val label: Vector                       = Vectors.fromBreeze(featuresMatrix * hiddenModel)
  val df: DataFrame                       = Matrices
    .fromBreeze(featuresMatrix)
    .rowIter
    .toSeq
    .zip(label.toArray)
    .toDF("features", "label")
    .repartition(10)
}
