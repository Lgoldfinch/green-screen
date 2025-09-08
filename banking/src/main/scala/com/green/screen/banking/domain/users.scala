package com.green.screen.banking.domain

import cats.Show
import com.green.screen.common.db.*
import eu.timepit.refined.types.all.*
import skunk.Codec
import skunk.codec.all.*
import io.circe.derivation.*
import io.circe.refined.*
import cats.syntax.all.*
import io.circe.{ Decoder, Encoder }

import java.util.UUID

object users {

  opaque type UserUuid = UUID

  object UserUuid {
    def apply(uuid: UUID): UserUuid = uuid

    extension (uuid: UserUuid) def value: UUID = uuid

    given Decoder[UserUuid] = Decoder.decodeUUID.map(UserUuid.apply)
  }

  val userUuidCodec: Codec[UserUuid] =
    uuid.imap(UserUuid.apply)(UserUuid.value)

  final case class User(uuid: UserUuid)

  object User {
    given Show[User] = Show.fromToString
  }

  val userCodec: Codec[User] = userUuidCodec.imap(User.apply)(_.uuid)

  opaque type UserScore = NonNegDouble

  object UserScore {
    def apply(value: NonNegDouble): UserScore = value

    extension (value: UserScore) def value: NonNegDouble = value

    given Encoder[UserScore] = Encoder.encodeDouble.contramap(_.value) // This could be wrong

  }

  val userScoreCodec: Codec[UserScore] = nonNegDoubleCodec.imap(UserScore.apply)(UserScore.value)
}
