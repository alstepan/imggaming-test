ThisBuild / version := "0.1.0"

ThisBuild / scalaVersion := "2.13.8"

lazy val root = (project in file("."))
  .settings(
    name := "imggaming",
    organization := "me.alstepan",
    assembly / mainClass := Some("imggaming.Application"),
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "3.2.12" % Test
    )
  )

ThisBuild / assemblyMergeStrategy := {
  case "application.conf" => MergeStrategy.concat
  case x =>
    val oldStrategy = (ThisBuild / assemblyMergeStrategy).value
    oldStrategy(x)
}