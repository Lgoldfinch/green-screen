package com.green.screen.analytics.engine.routes

import cats.effect.Concurrent
import cats.syntax.all.*
import com.green.screen.analytics.engine.programs.Perplexity
import com.green.screen.middlewares.UserType
import org.http4s.dsl.Http4sDsl
import org.http4s.*
import org.typelevel.log4cats.Logger

object PerplexityRoutes:
  def make[F[_]: Concurrent: Logger](
      perplexity: Perplexity[F]
  ): AuthedRoutes[UserType, F] = {
    val dsl = new Http4sDsl[F] {}
    import dsl.*

    AuthedRoutes.of { case authReq @ GET -> Root / "perplexity" as _ =>
      perplexity.run().flatMap(a => Ok(a.toString))
    }
  }
end PerplexityRoutes
