package com.green.screen.banking.algebras

import cats.effect.Resource
import cats.effect.kernel.{ Concurrent, MonadCancelThrow }
import cats.syntax.all.*
import UserOpenApiDataSql.*
import com.green.screen.banking.domain.openAPI.*
import skunk.syntax.all.*
import skunk.*

trait UserOpenApiData[F[_]]:
  def create(userOpenApiDataDB: UserOpenApiDataDB): F[Unit]
end UserOpenApiData

object UserOpenApiData:
  def make[F[_]](resource: Resource[F, Session[F]])(using MonadCancelThrow[F], Concurrent[F]): UserOpenApiData[F] =
    new UserOpenApiData[F] {
      override def create(userOpenApiDataDB: UserOpenApiDataDB): F[Unit] = resource.use(
        _.prepare(insertUserOpenApiDataCommand)
          .flatMap(
            _.execute(userOpenApiDataDB)
          )
          .void
      )
    }
end UserOpenApiData

object UserOpenApiDataSql:
  val insertUserOpenApiDataCommand: Command[UserOpenApiDataDB] =
    sql"""
               INSERT INTO user_open_api_data VALUES ($userOpenApiDataDBCodec)
             """.command
end UserOpenApiDataSql
