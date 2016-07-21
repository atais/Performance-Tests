/**
  * It is a simple project for testing performance of different Java/Scala aspects.
  */
lazy val basic = Project(
  "basic",
  file("."),
  settings = Defaults.coreDefaultSettings ++ Seq(
    name := "performance-tests",
    organization := "pl.msiatkowski",
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
