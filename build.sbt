/** This is the simplest possible use of ScalaMeter.
  * It allows running ScalaMeter benchmarks as part of the test suite.
  * It means, that when the test command is run, ScalaMeter benchmarks are run along
  * the tests from other test frameworks, such as ScalaTest or ScalaCheck.
  */
lazy val basic = Project(
  "basic",
  file("."),
  settings = Defaults.coreDefaultSettings ++ Seq(
    name := "scalameter-examples",
    organization := "com.storm-enroute",
    scalaVersion := "2.11.1",
    scalacOptions ++= Seq("-deprecation", "-unchecked", "-feature", "-Xlint"),
    publishArtifact := false,
    libraryDependencies ++= Seq(
      "com.storm-enroute" %% "scalameter" % "0.7" % "test"
    ),
    testFrameworks += new TestFramework("org.scalameter.ScalaMeterFramework"),
    //    parallelExecution in Test := false,
    logBuffered := false
  )
)
