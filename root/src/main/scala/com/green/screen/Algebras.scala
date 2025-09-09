package com.green.screen

import cats.effect.{ Concurrent, Resource }
import com.green.screen.banking.algebras.*
import org.typelevel.log4cats.Logger
import skunk.Session

trait Algebras[F[_]]:
  val companies: Companies[F]
  val transactions: OpenBankingTransactions[F]
  val users: Users[F]
  val userOpenBankingData: UserOpenBankingData[F]
end Algebras

object Algebras:
  def make[F[_]: {Concurrent, Logger}](postgres: Resource[F, Session[F]]): Algebras[F] =
    new Algebras[F] {
      override val companies: Companies[F]                     = Companies.make[F](postgres)
      override val transactions: OpenBankingTransactions[F]    = OpenBankingTransactions.make[F](postgres)
      override val users: Users[F]                             = Users.make[F](postgres)
      override val userOpenBankingData: UserOpenBankingData[F] = UserOpenBankingData.make[F](postgres)
    }
end Algebras
