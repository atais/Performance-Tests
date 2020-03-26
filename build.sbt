name := "performance-tests"
organization := "pl.msiatkowski"
scalaVersion := "2.12.11"

scalacOptions ++= Seq("-deprecation", "-unchecked", "-feature", "-Xlint")

libraryDependencies ++= Seq(
  "com.storm-enroute" %% "scalameter" % "0.19" % Test,
  "org.scalatest" %% "scalatest" % "3.1.1" % Test,
  "org.apache.commons" % "commons-lang3" % "3.5" % Test
)

testFrameworks += new TestFramework("org.scalameter.ScalaMeterFramework")
parallelExecution in Test := false
logBuffered := false
fork := true
outputStrategy := Some(StdoutOutput)
connectInput := true

publishArtifact := false

