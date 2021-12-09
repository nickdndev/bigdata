name := "spark_ml_linear_regression"

version := "1.0.0-SNAPSHOT"

scalaVersion := "2.12.12"

val sparkVersion = "3.0.1"
libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-sql"   % sparkVersion,
  "org.apache.spark" %% "spark-mllib" % sparkVersion,
  "org.scalatest"    %% "scalatest"   % "3.2.2" % "test"
)