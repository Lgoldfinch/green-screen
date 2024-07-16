import org.typelevel.sbt.tpolecat.*
import Dependencies.*

import scala.collection.immutable.Seq

ThisBuild / organization := "com.green.screen"
ThisBuild / scalaVersion := Version.ScalaVersion

lazy val root = (project in file("."))
  .enablePlugins(DockerPlugin, JavaServerAppPackaging)
  .settings(
  name := "green-screen",
    fork := true,
  libraryDependencies ++= Seq(
    "org.typelevel" %% "cats-effect" % Version.CatsEffect,
    "org.typelevel" %% "cats-effect-kernel" % Version.CatsEffect,
    "org.typelevel" %% "cats-effect-std" % Version.CatsEffect,
//    // HTTP
    "org.http4s" %% "http4s-core" % Version.Http4sVersion,
    "org.http4s" %% "http4s-dsl" % Version.Http4sVersion,
    "org.http4s" %% "http4s-circe" % Version.Http4sVersion,
    "org.http4s" %% "http4s-ember-server" % Version.Http4sVersion,
    "org.http4s" %% "http4s-ember-client" % Version.Http4sVersion,
    "org.http4s" %% "http4s-circe" % Version.Http4sVersion,
    "org.http4s" %% "http4s-dsl" % Version.Http4sVersion,

    // OTHER
    "org.tpolecat" %% "skunk-core" % Version.Skunk,
    "ch.qos.logback" % "logback-classic" % Version.Logback,
    // TEST
    "org.scalameta" %% "munit" % Version.Munit % Test,
    "org.typelevel" %% "munit-cats-effect" % Version.MunitCatsEffect % Test
  )).settings(
    Docker / packageName := packageName.value,
    Docker / version := version.value,
    dockerBaseImage := "openjdk:21",
    dockerExposedPorts ++= Seq(8080)
  )

//scalacOptions += "-Wnonunit-statement"
