package com.green.screen.analytics.engine.domain

import cats.Show
import io.circe.Decoder
import skunk.Codec
import skunk.codec.all.*
import users.UserUuid.*

import java.util.UUID

object users {

  opaque type UserUuid = UUID
  object UserUuid {
    def apply(uuid: UUID): UserUuid                = uuid
    extension (uuid: UserUuid) def value: UserUuid = uuid

    val userUuidCodec: Codec[UserUuid] =
      uuid.imap(UserUuid.apply)(_.value)

    implicit val userUuidDecoder: Decoder[UserUuid] = Decoder.decodeUUID.map(UserUuid.apply)

  }

  final case class User(uuid: UserUuid)

  object User {
    val userCodec: Codec[User] = userUuidCodec.imap(User.apply) { case User(userUuid) =>
      userUuid
    }

    implicit val userShow: Show[User] = Show.fromToString
  }
}
