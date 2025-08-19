package com.green.screen.ai.programs

import com.green.screen.ai.algebras.clients.PerplexityClient
import cats.MonadThrow
import com.green.screen.common.effects.GenUUID
import com.green.screen.ai.domain.perplexity.*
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
