import org.typelevel.sbt.tpolecat.*
import Dependencies.*

import scala.collection.immutable.Seq

ThisBuild / organization := "com.green.screen"
ThisBuild / scalaVersion := Version.ScalaVersion
ThisBuild / version      := "1.0.0"
ThisBuild / name         := "green-screen"

lazy val commonSettings = Seq(
  fork := true,
  scalafmtOnCompile := true
)

lazy val models = (project in file("models"))
  .enablePlugins()
  .settings(
    commonSettings,
    libraryDependencies ++= List.concat(
      CatsEffect,
      Circe,
      FlywayDb,
      Http4s,
      Logback,
      Logging,
      PureConfig,
      Refined,
      Skunk
    ) ++ List.concat(MunitTest,
      MunitCatsEffect, MunitCatsEffectScalaCheck,
      Weaver
    ).map(_ % Test),
    testFrameworks += new TestFramework("weaver.framework.CatsEffect")
  )

lazy val root = (project in file("src"))
  .enablePlugins(DockerPlugin, JavaServerAppPackaging)
  .dependsOn(models)
  .settings(
    commonSettings,
    libraryDependencies ++= List.concat(
    CatsEffect,
    Circe,
    FlywayDb,
    Http4s,
    Logback,
    Logging,
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
