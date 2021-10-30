name := "spark_tf_idf"

version := "1.0.0-SNAPSHOT"

scalaVersion := "2.12.8"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core"  % "3.2.0",
  "org.apache.spark" %% "spark-sql"   % "3.2.0"
)
