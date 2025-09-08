package com.green.screen.ai.routes

import cats.effect.Concurrent
import cats.syntax.all.*
import com.green.screen.ai.domain.common.AIAskRequest
import com.green.screen.ai.domain.perplexity
import com.green.screen.ai.programs.Perplexity
import com.green.screen.common.auth.UserType
import org.http4s.*
import org.http4s.circe.CirceEntityCodec.*
import org.http4s.dsl.Http4sDsl
import org.typelevel.log4cats.Logger

object PerplexityRoutes:
  def make[F[_]](
      perplexity: Perplexity[F]
  )(using Concurrent[F], Logger[F]): AuthedRoutes[UserType, F] = {
    val dsl = new Http4sDsl[F] {}
    import dsl.*

    AuthedRoutes.of { case authReq @ POST -> Root / "perplexity" as _ =>
      for {
        request            <- authReq.req.as[AIAskRequest]
        perplexityResponse <- perplexity.run(request)
        response           <- Ok(perplexityResponse)
      } yield response
    }
  }
end PerplexityRoutes
