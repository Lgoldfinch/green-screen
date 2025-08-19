package com.green.screen

import cats.data.Kleisli
import cats.effect.*
import com.comcast.ip4s.{ ipv4, port }
import com.green.screen.Algebras
import com.green.screen.Programs
import com.green.screen.db.SqlMigrator
import com.green.screen.http.{ Clients, Routes }
import com.green.screen.http.middlewares.{ AuthenticationMiddleware, ErrorHandlingMiddleware }
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.middleware.Logger
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.log4cats.slf4j.Slf4jLogger

object Server extends IOApp:
  def run(args: List[String]): IO[ExitCode] = {
    implicit val logger: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]
    (for {
      appConfig <- Resource.eval(ConfigLoader.loadConfig[IO])
      resources <- AppResources.make[IO](appConfig.db)
      _         <- Resource.eval(SqlMigrator[IO](appConfig.db).run)
      algebras = Algebras.make[IO](resources.postgres)
      clients  = Clients.make[IO](resources.client, appConfig.perplexity)
      programs = Programs.make[IO](algebras, clients)
      routes <- Resource.eval(Routes.make(programs))
      routesWithPatheticAuth = AuthenticationMiddleware[IO].authedMiddleware(routes).orNotFound
      httpAppWithLogging = Logger.httpApp[IO](
        logHeaders = true,
        logBody = true
      )(routesWithPatheticAuth)
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
