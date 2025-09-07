import Dependencies.*

ThisBuild / organization := "com.green.screen"
ThisBuild / scalaVersion := Version.ScalaVersion
ThisBuild / version      := "1.0.0"

lazy val commonSettings = Seq(
  fork := true,
  scalafmtOnCompile := true
)

lazy val common = (project in file("common"))
  .settings(
    name := "common",
    scalacOptions ++= Seq("-Xkind-projector"),
    commonSettings,
    libraryDependencies ++= List.concat(
      CatsEffect,
      Circe,
      PureConfig,
      Refined,
      Skunk
    ) ++ List.concat(MunitTest,
      MunitCatsEffect, MunitCatsEffectScalaCheck,
      Weaver
    ).map(_ % Test)
  )

lazy val ai = (project in file("ai"))
  .dependsOn(common, common % "test->test")
  .settings(
    name := "ai",
    commonSettings,
    libraryDependencies ++= List.concat(
      Http4s,
      Logback,
      Logging
    )
  )

lazy val banking = (project in file("banking"))
  .dependsOn(common, common % "test->test")
  .settings(
    name := "banking",
    commonSettings,
    libraryDependencies ++= List.concat(
      Http4s,
      Logback,
      Logging
    )
  )

// Root combines the different modules and packages the application as a Docker container
lazy val root = (project in file("root"))
  .enablePlugins(DockerPlugin, JavaServerAppPackaging)
  .dependsOn(ai, banking)
  .settings(
    name := "root",
    commonSettings,
    libraryDependencies ++= List.concat(
      FlywayDb
    ),
    Compile / run / mainClass := Some("com.green.screen.Server")
  )
  .settings(
    Docker / packageName := "green-screen",
    Docker / version := version.value,
    dockerBaseImage := "openjdk:21",
    dockerExposedPorts ++= Seq(8080)
)

Global / onChangedBuildSource := ReloadOnSourceChanges

addCommandAlias("run", "root / run")
addCommandAlias("compileAi", "ai / compile")
addCommandAlias("compileBanking", "banking / compile")
addCommandAlias("testAi", "ai / test")
addCommandAlias("testBanking", "banking / test")
