package com.green.screen.analytics.engine.domain

import java.time.Instant
import io.circe.*
import io.circe.syntax.*
import eu.timepit.refined.types.string.NonEmptyString

object common {
  implicit val nesDecoder: Decoder[NonEmptyString] = Decoder.decodeString.emap(NonEmptyString.from)

  opaque type CreatedAt = Instant
  object CreatedAt {
    def apply(f: Instant): CreatedAt = f

    extension (time: CreatedAt) def value: Instant = time

    implicit val createdAtDecoder: Decoder[CreatedAt] = Decoder.decodeInstant.map(CreatedAt(_))
    implicit val createdAtEncoder: Encoder[CreatedAt] = _.asJson
  }
}
