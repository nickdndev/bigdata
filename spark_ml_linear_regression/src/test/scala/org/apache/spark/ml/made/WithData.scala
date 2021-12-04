package org.apache.spark.ml.made

import org.apache.spark.ml.feature.VectorAssembler
import org.apache.spark.ml.made.WithSpark._sqlc
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.types.{DoubleType, StructType}

trait WithData extends WithSpark {

  lazy val schema: StructType = new StructType()
    .add("CRIM", DoubleType)
    .add("NOX", DoubleType)
    .add("RM", DoubleType)
    .add("DIS", DoubleType)
    .add("MEDV", DoubleType)

  private lazy val dataset_path: String = getClass.getResource("/spark_ml_dataset.csv").getPath

  lazy val dataset_raw_df: DataFrame = _sqlc
    .read
    .option("header", "true")
    .schema(schema)
    .csv(dataset_path)

  lazy val assembler: VectorAssembler = new VectorAssembler()
    .setInputCols(Array("CRIM", "NOX", "RM", "DIS"))
    .setOutputCol("features")

  lazy val df: DataFrame = assembler
    .transform(dataset_raw_df)
    .withColumnRenamed("MEDV", "label")
    .drop("CRIM", "NOX", "RM", "DIS")
}
