package com.green.screen.ai.domain

import eu.timepit.refined.types.string.NonEmptyString
import io.circe.derivation.{ Configuration, ConfiguredDecoder }
import io.circe.derivation.Configuration.default
import io.circe.refined.*

object common {

  given configuration: Configuration = default.withDefaults

  opaque type AIRequestMessage = NonEmptyString

  object AIRequestMessage {
    def apply(value: NonEmptyString): AIRequestMessage = value

    extension (r: AIRequestMessage) def value: NonEmptyString = r
  }

  final case class AIAskRequest(message: AIRequestMessage) derives ConfiguredDecoder
}
