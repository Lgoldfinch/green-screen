package com.green.screen.common

import io.circe.{ Decoder, Encoder }
import skunk.Codec
import skunk.codec.all.*

import java.time.{ Instant, LocalDateTime, ZoneOffset }

object misc {
  opaque type CreatedAt = Instant

  object CreatedAt {
    def apply(f: Instant): CreatedAt = f

    extension (time: CreatedAt) def value: Instant = time

    given Decoder[CreatedAt] = Decoder.decodeInstant.map(CreatedAt(_))

    given Encoder[CreatedAt] = Encoder.encodeInstant.contramap(_.value)

    def now: CreatedAt = CreatedAt(Instant.now)

    val createdAtCodec: Codec[CreatedAt] = {
      timestamp.imap[CreatedAt](ldt => CreatedAt(ldt.toInstant(ZoneOffset.UTC)))(createdAt =>
        LocalDateTime.ofInstant(createdAt.value, ZoneOffset.UTC)
      )
    }
  }
}
