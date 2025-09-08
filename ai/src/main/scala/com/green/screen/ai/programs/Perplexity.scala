package com.green.screen.ai.programs

import cats.MonadThrow
import cats.data.NonEmptyVector
import com.green.screen.ai.algebras.clients.PerplexityClient
import com.green.screen.ai.domain.common.AIAskRequest
import com.green.screen.ai.domain.perplexity.*
import com.green.screen.common.effects.GenUUID
import org.typelevel.log4cats.Logger

final class Perplexity[F[_]](
    perplexityClient: PerplexityClient[F]
)(using MonadThrow[F], Logger[F], GenUUID[F]):
  //  NonEmptyVector is hardcoded to length 1 for now. Later we can add more user and system prompts.
  def run(aiRequest: AIAskRequest): F[PerplexityResponse] =
    perplexityClient.chat(
      NonEmptyVector.one(PerplexityMessage(PerplexityRole.System, aiRequest.message.value))
    )
end Perplexity
