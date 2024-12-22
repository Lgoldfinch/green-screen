package com.green.screen.analytics.engine

import cats.effect.Concurrent
import cats.syntax.all.*
import com.green.screen.analytics.engine.domain.transactions.CreateTransactionRequest
import com.green.screen.analytics.engine.programs.ProcessTransaction
import org.http4s.HttpRoutes
import org.http4s.circe.CirceEntityDecoder.*
import org.http4s.dsl.Http4sDsl
import org.typelevel.log4cats.Logger
import com.green.screen.analytics.engine.programs.ProcessTransaction.CompanyNotFound

object AnalyticsEngineRoutes {
  def analyticsRoutes[F[_]: Concurrent: Logger](processTransaction: ProcessTransaction[F]): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F] {}
    import dsl.*
    HttpRoutes.of[F] {
      case req @ POST -> Root / "transactions" =>
        req
          .as[CreateTransactionRequest]
          .flatMap(createTransactionRequest =>
            processTransaction.createTransaction(createTransactionRequest).flatMap(Created(_)).recoverWith {
              case err: CompanyNotFound =>
                NotFound(err.getMessage)
            }
          )
      case GET -> Root / "transactions" =>
        ???
    }
  }
}
