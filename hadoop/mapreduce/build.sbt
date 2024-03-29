name := "hadoop"

version := "0.1"

scalaVersion := "2.13.6"

libraryDependencies += "org.apache.hadoop" % "hadoop-client" % "2.9.0"

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.first
}