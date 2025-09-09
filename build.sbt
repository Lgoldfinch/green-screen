import Dependencies.*

ThisBuild / organization := "com.green.screen"
ThisBuild / scalaVersion := Version.ScalaVersion
ThisBuild / version      := "1.0.0"

lazy val commonDependencies = libraryDependencies ++= List.concat(
  Http4s,
  Logback,
  Logging
)

lazy val common = (project in file("common"))
  .settings(
    name := "common",
    fork := true,
    scalafmtOnCompile := true,
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
    commonDependencies
  )

lazy val banking = (project in file("banking"))
  .dependsOn(common, common % "test->test")
  .settings(
    name := "banking",
    commonDependencies
  )

lazy val root = (project in file("root"))
  .dependsOn(ai, banking)
  .settings(
    name := "root",
    libraryDependencies ++= List.concat(
      FlywayDb
    ),
    Compile / run / mainClass := Some("com.green.screen.Server")
  )

Global / onChangedBuildSource := ReloadOnSourceChanges

scalacOptions ++= Seq(
  "-deprecation",
  "-feature",
  "-language:noAutoTupling",
  "-language:strictEquality",
  "-Werror",
  "-Wnonunit-statements",
  "-Wunused:all",
  "-Ysafe-init"
)

addCommandAlias("run", "root / run")
addCommandAlias("compileAi", "ai / compile")
addCommandAlias("compileBanking", "banking / compile")
addCommandAlias("testAi", "ai / test")
addCommandAlias("testBanking", "banking / test")