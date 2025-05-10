import org.typelevel.sbt.tpolecat.*
import Dependencies.*

import scala.collection.immutable.Seq

ThisBuild / organization := "com.green.screen"
ThisBuild / scalaVersion := Version.ScalaVersion
ThisBuild / version      := "1.0.0"

lazy val root = (project in file("."))
  .enablePlugins(DockerPlugin, JavaServerAppPackaging)
  .settings(
  name := "green-screen",
    fork := true,
    scalafmtOnCompile := true,
    libraryDependencies ++= List.concat(
    CatsEffect,
    Circe,
    FlywayDb,
    Http4s,
    Logback,
    Logging,
    NimbusJoseJwt,
    PureConfig,
    Refined,
    Skunk
  ) ++ List.concat(MunitTest,
    MunitCatsEffect, MunitCatsEffectScalaCheck,
    Weaver
  ).map(_ % Test),
    testFrameworks += new TestFramework("weaver.framework.CatsEffect")
)
  .settings(
    Docker / packageName := "green-screen",
    Docker / version := version.value,
    dockerBaseImage := "openjdk:21",
    dockerExposedPorts ++= Seq(8080)
)

//scalacOptions += "-Wnonunit-statement"
