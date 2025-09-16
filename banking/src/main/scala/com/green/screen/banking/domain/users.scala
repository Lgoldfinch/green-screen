package com.green.screen.banking.domain

import cats.Show
import com.green.screen.common.db.*
import eu.timepit.refined.types.all.*
import io.circe.derivation.{Configuration, ConfiguredEncoder}
import io.circe.derivation.Configuration.default
import io.circe.{Decoder, Encoder}
import skunk.Codec
import skunk.codec.all.*
import io.circe.refined.*

import java.util.UUID

object users {

  given Configuration = default.withPascalCaseMemberNames

  opaque type UserUuid = UUID

  object UserUuid {
    def apply(uuid: UUID): UserUuid = uuid

    extension (uuid: UserUuid) def value: UUID = uuid

    given Encoder[UserUuid] = Encoder.encodeUUID.contramap(_.value)

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

    given Encoder[UserScore] = Encoder.encodeDouble.contramap(_.value)
  }

  val userScoreCodec: Codec[UserScore] = nonNegDoubleCodec.imap(UserScore.apply)(UserScore.value)
  
  final case class GetUserScoreResponse(userUuid: UserUuid, score: UserScore) derives ConfiguredEncoder
}