package com.green.screen

import cats.effect.*
import com.comcast.ip4s.{ipv4, port}
import com.green.screen.analytics.engine.AnalyticsEngineRoutes
import com.green.screen.analytics.engine.algebras.Algebras
import com.green.screen.analytics.engine.programs.ProcessTransaction
import org.http4s.ember.server.EmberServerBuilder
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.log4cats.slf4j.Slf4jLogger

object Server extends IOApp:
  def run(args: List[String]): IO[ExitCode] = {
    implicit val logger: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]
    (for {
        resources <- AppResources.make[IO]
        _ <- Resource.eval(SqlMigrator[IO]("jdbc:postgresql://localhost:5432/green-screen-postgres").run)
        algebras = Algebras.make[IO](resources.postgres)
        processTransaction = ProcessTransaction[IO](algebras.companies, algebras.transactions)
        httpApp = AnalyticsEngineRoutes.analyticsRoutes[IO](processTransaction).orNotFound
        _ <-
          EmberServerBuilder.default[IO]
            .withHost(ipv4"0.0.0.0")
            .withPort(port"8080")
            .withHttpApp(httpApp)
            .build
      } yield ()).useForever
}