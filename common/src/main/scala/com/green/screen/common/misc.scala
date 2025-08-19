package com.green.screen.common

import eu.timepit.refined.types.string.NonEmptyString
import io.circe.*
import io.circe.syntax.*
import skunk.Codec
import skunk.codec.all.*

import java.time.{ Instant, LocalDateTime, ZoneOffset }

object misc {
  implicit val nesDecoder: Decoder[NonEmptyString] = Decoder.decodeString.emap(NonEmptyString.from)

  implicit val nesEncoder: Encoder[NonEmptyString] = Encoder.encodeString.contramap(_.value)

  opaque type CreatedAt = Instant

  object CreatedAt {
    def apply(f: Instant): CreatedAt = f

    extension (time: CreatedAt) def value: Instant = time

    implicit val createdAtDecoder: Decoder[CreatedAt] = Decoder.decodeInstant.map(CreatedAt(_))
    implicit val createdAtEncoder: Encoder[CreatedAt] = _.asJson

    def now: CreatedAt = CreatedAt(Instant.now)

    val createdAtCodec: Codec[CreatedAt] =
      timestamp.imap[CreatedAt](ldt => CreatedAt(ldt.toInstant(ZoneOffset.UTC)))(createdAt =>
        LocalDateTime.ofInstant(createdAt.value, ZoneOffset.UTC)
      )
  }
}
