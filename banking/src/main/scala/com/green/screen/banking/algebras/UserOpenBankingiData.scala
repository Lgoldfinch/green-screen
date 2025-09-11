package com.green.screen.banking.algebras

import cats.effect.Resource
import cats.effect.kernel.{ Concurrent, MonadCancelThrow }
import cats.syntax.all.*
import UserOpenBankingDataSql.*
import com.green.screen.banking.domain.openBanking.*
import skunk.syntax.all.*
import skunk.*

trait UserOpenBankingData[F[_]]:
  def create(userOpenBankingDataDB: UserOpenBankingDataDB): F[Unit]
end UserOpenBankingData

object UserOpenBankingData:
  def make[F[_]](postgres: Resource[F, Session[F]])(using MonadCancelThrow[F], Concurrent[F]): UserOpenBankingData[F] =
    new UserOpenBankingData[F] {
      override def create(userOpenBankingDataDB: UserOpenBankingDataDB): F[Unit] = postgres.use(
        _.prepare(insertUserOpenBankingDataCommand)
          .flatMap(
            _.execute(userOpenBankingDataDB)
          )
          .void
      )
    }
end UserOpenBankingData

object UserOpenBankingDataSql:
  val insertUserOpenBankingDataCommand: Command[UserOpenBankingDataDB] =
    sql"""
               INSERT INTO user_open_banking_data VALUES ($userOpenBankingDataDBCodec)
             """.command
end UserOpenBankingDataSql
