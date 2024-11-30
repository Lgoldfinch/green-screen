package com.green.screen

import cats.effect.*
import com.comcast.ip4s.{ipv4, port}
import com.green.screen.analytics.engine.AnalyticsEngineRoutes
import org.http4s.ember.server.EmberServerBuilder
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.log4cats.slf4j.Slf4jLogger


//    val url: String = s"jdbc:postgresql://$host:$port/$name?options=-c%20search_path=$name"
object Server extends IOApp:
  def run(args: List[String]): IO[ExitCode] = {
    implicit val logger: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]

    (for {
        resources <- AppResources.make[IO]
        _ <- Resource.eval(SqlMigrator[IO]("jdbc:postgresql://localhost:5432/green-screen-postgres").run)
        httpApp = AnalyticsEngineRoutes.analyticsRoutes[IO]("Fuck", resources.postgres).orNotFound
        _ <-
          EmberServerBuilder.default[IO]
            .withHost(ipv4"0.0.0.0")
            .withPort(port"8080")
            .withHttpApp(httpApp)
            .build
      } yield ()).useForever
}