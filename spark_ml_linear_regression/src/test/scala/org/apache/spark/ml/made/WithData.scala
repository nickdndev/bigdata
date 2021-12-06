package org.apache.spark.ml.made

import breeze.linalg.DenseVector
import org.apache.spark.ml.linalg.Vectors
import org.apache.spark.sql.{DataFrame, functions}

trait WithData extends WithSpark {
  import sqlc.implicits._

  lazy val df: DataFrame = Seq
    .fill(10000)(Vectors.fromBreeze(DenseVector.rand(3)))
    .map(x => Tuple1(x))
    .toDF("features")
    .withColumn("label", (functions.rand() * functions.lit(0.1) - functions.lit(0.05)).as("label"))
}
