package com.green.screen.ai.algebras.clients

import cats.ApplicativeThrow
import cats.data.NonEmptyVector
import cats.effect.*
import cats.syntax.all.*
import com.green.screen.ai.domain.Config.PerplexityConfig
import com.green.screen.ai.domain.perplexity.*
import io.circe.syntax.*
import org.http4s.circe.*
import org.http4s.circe.CirceEntityCodec.circeEntityDecoder
import org.http4s.client.*
import org.http4s.*
import org.typelevel.ci.CIString
import org.typelevel.log4cats.Logger

trait PerplexityClient[F[_]]:
  def chat(messages: List[PerplexityMessage]): F[PerplexityResponse]
end PerplexityClient

object PerplexityClient {
  def make[F[_]: Concurrent: Logger](
      httpClient: Client[F],
      config: PerplexityConfig
  ): PerplexityClient[F] = {
    new PerplexityClient[F] {

      private val authHeader        = Header.Raw(CIString("Authorization"), s"Bearer ${config.apiKey}")
      private val contentTypeHeader = Header.Raw(CIString("Content-Type"), "application/json")

      override def chat(messages: List[PerplexityMessage]): F[PerplexityResponse] = {
        import eu.timepit.refined.auto.autoUnwrap
        for {
          uri <- Uri.fromString(config.baseUri.value).map(_.addPath("chat").addPath("completions")).liftTo[F]
          requestBody <- PerplexityRequest
            .buildRequest(config.model, NonEmptyVector.fromVectorUnsafe(messages.toVector))
            .liftTo[F]
          request = Request[F](
            method = Method.POST,
            uri = uri
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
