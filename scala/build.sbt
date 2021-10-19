name := "linear_regression_model"

version := "1.0.0-SNAPSHOT"
scalaVersion := "2.13.3"

libraryDependencies ++= Seq(
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "org.slf4j"      % "slf4j-simple"    % "1.7.32",
  "org.slf4j"      % "slf4j-api"       % "1.7.32",
  "org.typelevel" %% "cats-core"       % "2.3.0",
  "org.scalanlp"  %% "breeze"          % "1.1",
  "org.scalanlp"  %% "breeze-natives"  % "1.1",
  "org.scalanlp"  %% "breeze-viz"      % "1.1"
)
