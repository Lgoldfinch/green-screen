package com.green.screen.analytics.engine

import cats.effect.Concurrent
import cats.syntax.all.*
import com.green.screen.analytics.engine.domain.transactions.CreateTransactionRequest
import com.green.screen.analytics.engine.programs.ProcessTransaction
import org.http4s.HttpRoutes
import org.http4s.circe.CirceEntityDecoder.*
import org.http4s.dsl.Http4sDsl
import java.time.Instant

object AnalyticsEngineRoutes {
  def analyticsRoutes[F[_]: Concurrent](processTransaction: ProcessTransaction[F]): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F] {}
    import dsl.*
    HttpRoutes.of[F] {
      case req @ POST -> Root / "transactions" =>
        for {
          request <- req.as[CreateTransactionRequest]
          _ <- processTransaction.run(request)
          resp <- Created()
        } yield resp

      case GET -> Root / "transactions" =>
        for {
          _ <- processTransaction.runAgain
          resp <- Created()
        } yield resp
    }
  }
}