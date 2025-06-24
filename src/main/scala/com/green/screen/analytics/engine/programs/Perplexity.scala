package com.green.screen.analytics.engine.programs

import cats.MonadThrow
import com.green.screen.analytics.engine.algebras.clients.PerplexityClient
import com.green.screen.analytics.engine.domain.*
import com.green.screen.analytics.engine.domain.perplexity.*
import com.green.screen.common.domain.effects.GenUUID
import eu.timepit.refined.types.string.NonEmptyString
import org.typelevel.log4cats.Logger

final class Perplexity[F[_]: MonadThrow: Logger: GenUUID](
    perplexityClient: PerplexityClient[F]
):
  def run(): F[PerplexityResponse] = {
    val message: NonEmptyString = NonEmptyString.unsafeFrom("Find me the cheapest pink t-shirt")
    perplexityClient.chat(
      List(PerplexityMessage(PerplexityRole.System, message))
    )
  }
end Perplexity
