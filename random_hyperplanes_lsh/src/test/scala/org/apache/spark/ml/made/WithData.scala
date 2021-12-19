package org.apache.spark.ml.made

import org.apache.spark.ml.linalg.Vectors
import org.apache.spark.sql.DataFrame

trait WithData  extends WithSpark {
  lazy val _vectors = Seq(
    Vectors.dense(1, 2, 3, 4),
    Vectors.dense(5, 4, 9, 7),
    Vectors.dense(9, 6, 4, 5)
  )

  lazy val _hyperplanes = Array(
    Vectors.dense(1, -1, 1, 1),
    Vectors.dense(-1, 1, -1, 1),
    Vectors.dense(1, 1, -1, -1)
  )

  lazy val _data: DataFrame = {
    import sqlc.implicits._
    _vectors.map(x => Tuple1(x)).toDF("features")
  }
}

