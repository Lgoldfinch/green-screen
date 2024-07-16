package com.green.screen.analytics.engine.domain

import cats.Show
import io.circe.{ Decoder, Encoder }
import skunk.Codec
import skunk.codec.all.*
import UserUuid.*
import eu.timepit.refined.types.all.*
import java.util.UUID
import com.green.screen.common.domain.skunks.*

final case class User(uuid: UserUuid)

object User {
  val userCodec: Codec[User] = userUuidCodec.imap(User.apply) { case User(userUuid) =>
    userUuid
  }

  implicit val userShow: Show[User] = Show.fromToString
}

opaque type UserScore = NonNegDouble

object UserScore {
  def apply(value: NonNegDouble): UserScore            = value
  extension (value: UserScore) def value: NonNegDouble = value

  val userScoreCodec: Codec[UserScore]       = nonNegDoubleCodec.imap(UserScore.apply)(_.value)
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
