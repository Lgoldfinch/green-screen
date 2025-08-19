package com.green.screen.banking.algebras

import cats.effect.Resource
import cats.effect.kernel.{ Concurrent, MonadCancelThrow }
import cats.syntax.all.*
import UserOpenApiDataSql.*
import com.green.screen.banking.domain.UserOpenApiDataDB.*
import com.green.screen.banking.domain.*
import com.green.screen.banking.domain.UserOpenApiDataDB
import skunk.syntax.all.*
import skunk.*

trait UserOpenApiData[F[_]]:
  def create(userOpenApiDataDB: UserOpenApiDataDB): F[Unit]
end UserOpenApiData

object UserOpenApiData:
  def make[F[_]: MonadCancelThrow: Concurrent](resource: Resource[F, Session[F]]): UserOpenApiData[F] =
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
