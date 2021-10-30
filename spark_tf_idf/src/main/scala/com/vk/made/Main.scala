package com.vk.made

import org.apache.spark.sql._
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions._

object Main extends App {
  val spark = SparkSession
    .builder()
    .master("local[*]")
    .appName("tfidf app")
    .getOrCreate()

  import spark.implicits._

  val inputData: String = "data/tripadvisor_hotel_reviews.csv"

  val dataDF = spark
    .read
    .option("inferSchema", "true")
    .option("header", "true")
    .csv(inputData)
    .select(regexp_replace(lower(col("Review")), "[^\\w\\s-]", "").as("review"))
    .map { review =>
      review
        .mkString
        .split(" ")
        .filter(p => p.trim.nonEmpty)
    }

  val dataWithIdDF = dataDF
    .withColumn("review_id", monotonically_increasing_id())
    .withColumn("token", explode($"value"))
    .drop("value")

  val dataWithIdCountDF = dataWithIdDF
    .groupBy("review_id", "token")
    .count()

  val idfDF = dataWithIdCountDF
    .groupBy(col("token"))
    .agg(count(col("review_id")).as("freq"))
    .orderBy(desc("freq"))
    .limit(100)
    .select(col("token"), log(lit(dataDF.count) / (col("freq") + 1) + 1).as("idf"))

  broadcast(idfDF)

  val topWordDF = idfDF
    .select(col("token"))
    .collect
    .map(_.getString(0))

  val tfDF = dataWithIdCountDF
    .withColumn("len", sum("count").over(Window.partitionBy("review_id")))
    .filter(col("token").isin(topWordDF: _*))
    .withColumn("tf", col("count") / col("len"))
    .select(col("token"), col("review_id"), col("tf"))

  val tfidfDF = tfDF
    .join(idfDF, "token")
    .withColumn("tfidf", col("tf") * col("idf"))
    .select(col("token"), col("review_id"), col("tfidf"))

  val result = tfidfDF
    .groupBy("review_id")
    .pivot(col("token"))
    .sum("tfidf")

  result.show(5)
}
