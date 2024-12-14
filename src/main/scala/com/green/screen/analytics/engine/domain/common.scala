package com.green.screen.analytics.engine.domain

import java.time.Instant
import io.circe.*
import io.circe.syntax.*

object common {

  opaque type CreatedAt = Instant
  object CreatedAt {
    def apply(f: Instant): CreatedAt = f

    extension (time: CreatedAt)
      def value: Instant = time

    implicit val createdAtDecoder: Decoder[CreatedAt] = _.get[CreatedAt]("created_at")
    implicit val createdAtEncoder: Encoder[CreatedAt] = _.asJson
  }
}
