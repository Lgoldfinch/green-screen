package com.green.screen.middlewares

import cats.ApplicativeThrow
import cats.data.Kleisli
import org.http4s.*
import org.typelevel.log4cats.Logger
import cats.syntax.all.*

object ErrorHandlingMiddleware {
  def apply[F[_]: ApplicativeThrow: Logger](app: HttpApp[F]): HttpApp[F] = Kleisli {
    req =>
      app(req)
        .recoverWith {
          case err @ InvalidMessageBodyFailure(details, cause) =>
            val reason = cause.flatMap( err => Option(err.getMessage)).getOrElse("Unknown")
            val errorMsg = s"The request body was invalid. Reason: $reason"

            Logger[F].warn(errorMsg) *> Response[F](Status.UnprocessableEntity).withEntity(errorMsg).pure[F]
        }
        .onError {
          case err => Logger[F].warn(Option(err.getMessage).getOrElse("No error message found"))
        }
  }
}