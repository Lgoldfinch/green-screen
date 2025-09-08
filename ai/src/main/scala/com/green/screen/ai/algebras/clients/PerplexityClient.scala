package com.green.screen.ai.algebras.clients

import cats.ApplicativeThrow
import cats.data.NonEmptyVector
import cats.effect.*
import cats.syntax.all.*
import com.green.screen.ai.domain.PerplexityConfig
import com.green.screen.ai.domain.perplexity.*
import com.green.screen.ai.domain.PerplexityBaseUri.stringify
import io.circe.syntax.*
import org.http4s.circe.*
import org.http4s.circe.CirceEntityCodec.circeEntityDecoder
import org.http4s.client.*
import org.http4s.*
import org.typelevel.ci.CIString
import org.typelevel.log4cats.Logger

trait PerplexityClient[F[_]]:
  def chat(messages: NonEmptyVector[PerplexityMessage]): F[PerplexityResponse]
end PerplexityClient

object PerplexityClient {
  def make[F[_]](
      httpClient: Client[F],
      config: PerplexityConfig
  )(using Concurrent[F], Logger[F]): PerplexityClient[F] = {
    new PerplexityClient[F] {
      private val authHeader        = Header.Raw(CIString("Authorization"), s"Bearer ${config.apiKey}")
      private val contentTypeHeader = Header.Raw(CIString("Content-Type"), "application/json")

      override def chat(messages: NonEmptyVector[PerplexityMessage]): F[PerplexityResponse] = {
        for {
          uri <- Uri.fromString(config.baseUri.stringify).map(_.addPath("chat").addPath("completions")).liftTo[F]
          requestBody <- PerplexityRequest
            .buildRequest(config.model, messages)
            .liftTo[F]
          request = Request[F](
            Method.POST,
            uri
          ).withHeaders(Headers(authHeader, contentTypeHeader))
            .withEntity(requestBody.asJson)
          response <- httpClient.run(request).use { response =>
            response.status match {
              case Status.Ok => response.as[PerplexityResponse]
              case Status.BadRequest =>
                response
                  .as[PerplexityErrorResponse]
                  .flatMap(
                    ApplicativeThrow[F].raiseError
                  )
              case statusCode =>
                val errorMsg = PerplexityErrorResponse(
                  PerplexityErrorDetail(s"Unhandled status code. Status code $statusCode", "unknown")
                )
                ApplicativeThrow[F].raiseError(errorMsg)
            }
          }
        } yield response
      }
    }
  }
}
