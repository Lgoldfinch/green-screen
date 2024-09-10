import sbt.*

object Dependencies {
  object Version {
    val ScalaVersion = "3.4.2"
    val CatsEffect = "3.5.4"
    val Http4s = "0.23.27"
    val Skunk = "0.6.4"
    val Logback = "1.2.6"
    val FlywayDb     = "9.11.0"
    val PostgresJdbc = "42.5.4"

    // TEST
    val Munit = "0.7.29"
    val MunitCatsEffect = "2.0.0"
  }

  val CatsEffect = List(
    "org.typelevel" %% "cats-effect",
    "org.typelevel" %% "cats-effect-kernel",
    "org.typelevel" %% "cats-effect-std",
  ).map(_ % Version.CatsEffect)

  val Http4s = List(
    "org.http4s" %% "http4s-core",
    "org.http4s" %% "http4s-dsl",
    "org.http4s" %% "http4s-circe",
    "org.http4s" %% "http4s-ember-server",
    "org.http4s" %% "http4s-ember-client",
    "org.http4s" %% "http4s-circe",
    "org.http4s" %% "http4s-dsl",
  ).map(_ % Version.Http4s)

  val Logback = List( "ch.qos.logback" % "logback-classic" % Version.Logback)

  val Skunk = List(
    "org.tpolecat" %% "skunk-core" % Version.Skunk,
  )

  // TEST
  val MunitTest = List(
    "org.scalameta" %% "munit",
    "org.scalameta" %% "munit-scalacheck"
  ).map(_ % Version.Munit)

  val MunitCatsEffect = List(
    "org.typelevel" %% "munit-cats-effect" % Version.MunitCatsEffect
  )

}