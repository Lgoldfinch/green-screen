package com.green.screen.ai.routes

import cats.effect.Concurrent
import cats.syntax.all.*
import com.green.screen.ai.programs.Perplexity
import com.green.screen.common.auth.UserType
import org.http4s.*
import org.http4s.dsl.Http4sDsl
import org.typelevel.log4cats.Logger

object PerplexityRoutes:
  def make[F[_]](
      perplexity: Perplexity[F]
  )(using Concurrent[F], Logger[F]): AuthedRoutes[UserType, F] = {
    val dsl = new Http4sDsl[F] {}
    import dsl.*

    AuthedRoutes.of { case authReq @ GET -> Root / "perplexity" as _ =>
      perplexity.run().flatMap(a => Ok(a.toString))
    }
  }
end PerplexityRoutes
