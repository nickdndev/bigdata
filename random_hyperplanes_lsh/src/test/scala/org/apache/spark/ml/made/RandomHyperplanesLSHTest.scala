package org.apache.spark.ml.made

import org.apache.spark.ml.linalg
import org.apache.spark.ml.linalg.Vector
import org.apache.spark.sql.DataFrame
import org.scalatest.flatspec._
import org.scalatest.matchers._

class RandomHyperplanesLSHTest extends AnyFlatSpec with should.Matchers with WithSpark with WithData {
  val delta                           = 0.0001
  lazy val data: DataFrame            = _data
  lazy val vectors: Seq[Vector]       = _vectors
  lazy val hyperplanes: Array[Vector] = _hyperplanes

  "Model" should "test hash function" in {
    val randomHyperplanesLSHModel = new RandomHyperplanesLSHModel(randHyperPlanes = hyperplanes)
      .setInputCol("features")
      .setOutputCol("hashes")
    val testVector                = linalg.Vectors.fromBreeze(breeze.linalg.Vector(3, 4, 5, 6))
    val sketch                    = randomHyperplanesLSHModel.hashFunction(testVector)

    sketch.length should be(3)
    sketch(0)(0) should be(1.0)
    sketch(1)(0) should be(1.0)
    sketch(2)(0) should be(-1.0)
  }

  "Model" should "test hash distance" in {
    val randomHyperplanesLSHModel = new RandomHyperplanesLSHModel(
      randHyperPlanes = hyperplanes
    ).setInputCol("features")
      .setOutputCol("hashes")
    val testVector1               = linalg.Vectors.fromBreeze(breeze.linalg.Vector(3, 4, 5, 6))
    val sketch1                   = randomHyperplanesLSHModel.hashFunction(testVector1)
    val testVector2               = linalg.Vectors.fromBreeze(breeze.linalg.Vector(4, 3, 2, 1))
    val sketch2                   = randomHyperplanesLSHModel.hashFunction(testVector2)
    val similarity                = randomHyperplanesLSHModel.hashDistance(sketch1, sketch2)
    sketch1.foreach(println)
    sketch2.foreach(println)

    similarity should be((1.0 / 3.0) +- delta)
  }

  "Model" should "test key distance" in {
    val randomHyperplanesLSHModel = new RandomHyperplanesLSHModel(
      randHyperPlanes = hyperplanes
    ).setInputCol("features")
      .setOutputCol("hashes")
    val testVector1               = linalg.Vectors.fromBreeze(breeze.linalg.Vector(3, 4, 5, 6))
    val testVector2               = linalg.Vectors.fromBreeze(breeze.linalg.Vector(4, 3, 2, 1))
    val keyDistance               = randomHyperplanesLSHModel.keyDistance(testVector1, testVector2)

    keyDistance should be(1 - 0.7875 +- delta)
  }

  "Model" should "transform data" in {
    val randomHyperplanesLSH = new RandomHyperplanesLSH(
    ).setNumHashTables(2)
      .setInputCol("features")
      .setOutputCol("hashes")
    val model                = randomHyperplanesLSH.fit(data)
    val transformedData      = model.transform(data)

    transformedData.count() should be(3)
  }

  "Model" should "approx similarity join" in {
    val model = new RandomHyperplanesLSHModel(
      randHyperPlanes = hyperplanes
    ).setInputCol("features")
      .setOutputCol("hashes")

    val approxData = model.approxSimilarityJoin(data, data, 1)
    approxData.count() should be(9)
  }
}
