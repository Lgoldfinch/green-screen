package com.green.screen.analytics.engine.domain

import cats.Show
import io.circe.{ Decoder, Encoder }
import skunk.Codec
import skunk.codec.all.*
import UserUuid.*

import java.util.UUID

final case class User(uuid: UserUuid)

object User {
  val userCodec: Codec[User] = userUuidCodec.imap(User.apply) { case User(userUuid) =>
    userUuid
  }

  implicit val userShow: Show[User] = Show.fromToString
}

opaque type UserScore = Double
object UserScore {
  def apply(value: Double): UserScore            = value
  extension (value: UserScore) def value: Double = value

  val userScoreCodec: Codec[UserScore]       = float8.imap(UserScore.apply)(_.value)
  implicit val userScore: Encoder[UserScore] = Encoder.encodeDouble.contramap(_.value)
}

opaque type UserUuid = UUID
object UserUuid {
  def apply(uuid: UUID): UserUuid            = uuid
  extension (uuid: UserUuid) def value: UUID = uuid

  val userUuidCodec: Codec[UserUuid] =
    uuid.imap(UserUuid.apply)(_.value)

  implicit val userUuidDecoder: Decoder[UserUuid] = Decoder.decodeUUID.map(UserUuid.apply)
}
