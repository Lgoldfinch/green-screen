package com.green.screen

import cats.data.Kleisli
import cats.effect.*
import com.comcast.ip4s.{ ipv4, port }
import com.green.screen.analytics.engine.*
import com.green.screen.analytics.engine.algebras.Algebras
import com.green.screen.analytics.engine.programs.*
import com.green.screen.analytics.engine.routes.Routes
import com.green.screen.middlewares.ErrorHandlingMiddleware
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.middleware.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import org.typelevel.log4cats.SelfAwareStructuredLogger

object Server extends IOApp:
  def run(args: List[String]): IO[ExitCode] = {
    implicit val logger: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]
    (for {
      resources <- AppResources.make[IO]
      _         <- Resource.eval(SqlMigrator[IO]("jdbc:postgresql://localhost:5432/green-screen-postgres").run)
      algebras = Algebras.make[IO](resources.postgres)
      programs = Programs.make[IO](algebras)
      routes   = Routes.make(programs).orNotFound
      httpAppWithLogging = Logger.httpApp[IO](
        logHeaders = true,
        logBody = true
      )(routes)
      _ <-
        EmberServerBuilder
          .default[IO]
          .withHost(ipv4"0.0.0.0")
          .withPort(port"8080")
          .withHttpApp(ErrorHandlingMiddleware(httpAppWithLogging))
          .build
    } yield ()).useForever
  }
end Server
