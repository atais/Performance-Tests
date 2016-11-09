/**
  * It is a simple project for testing performance of different Java/Scala aspects.
  */
lazy val basic = Project(
  "basic",
  file("."),
  settings = Defaults.coreDefaultSettings ++ Seq(
    name := "performance-tests",
    organization := "pl.msiatkowski",
    scalaVersion := "2.11.8",
    scalacOptions ++= Seq("-deprecation", "-unchecked", "-feature", "-Xlint"),
    publishArtifact := false,
    libraryDependencies ++= Seq(
      "com.storm-enroute" %% "scalameter" % "0.7" % "test",
      "org.scalatest" %% "scalatest" % "3.0.0" % "test",
      "org.apache.commons" % "commons-lang3" % "3.5"
    ),
    resolvers ++= Seq(
      "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
      "Sonatype OSS Releases" at "https://oss.sonatype.org/content/repositories/releases"
    ),
    testFrameworks += new TestFramework("org.scalameter.ScalaMeterFramework"),
    //    parallelExecution in Test := false,
    logBuffered := false
  )
)
