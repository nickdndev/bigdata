name := "linear_regression_model"

version := "1.0.0-SNAPSHOT"
scalaVersion := "2.13.3"

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-core"      % "2.3.0",
  "org.scalanlp"  %% "breeze"         % "1.1",
  "org.scalanlp"  %% "breeze-natives" % "1.1",
  "org.scalanlp"  %% "breeze-viz"     % "1.1"
)
