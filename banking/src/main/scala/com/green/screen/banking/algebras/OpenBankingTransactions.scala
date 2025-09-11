package com.green.screen.banking.algebras

import cats.effect.kernel.{Concurrent, MonadCancelThrow, Resource}
import cats.syntax.all.*
import com.green.screen.*
import com.green.screen.banking.algebras.OpenBankingTransactionsSQL.*
import com.green.screen.banking.domain.companies.*
import com.green.screen.banking.domain.transactions.*
import org.typelevel.log4cats.Logger
import skunk.*
import skunk.syntax.all.*

trait OpenBankingTransactions[F[_]] {
  def createTransaction(transaction: OpenBankingTransaction): F[Unit]
  def getTransactions(companyUuid: CompanyUuid): F[List[OpenBankingTransaction]]
}

object OpenBankingTransactions:
  def make[F[_]: {MonadCancelThrow, Concurrent, Logger}](
      postgres: Resource[F, Session[F]]
  ): OpenBankingTransactions[F] =
    new OpenBankingTransactions[F]:

      override def createTransaction(transaction: OpenBankingTransaction): F[Unit] = postgres.use(session =>
          for {
            command <- session.prepare(insertTransaction)
            _       <- command.execute(transaction)
          } yield ()
        )

      override def getTransactions(companyUuid: CompanyUuid): F[List[OpenBankingTransaction]] = {
        postgres.use(session =>
          for {
            query        <- session.prepare(getTransactionsByCompanyUuid)
            transactions <- query.stream(companyUuid, 32).compile.toList
          } yield transactions
        )
      }

end OpenBankingTransactions

object OpenBankingTransactionsSQL:
  val insertTransaction: Command[OpenBankingTransaction] =
    sql"""
         INSERT INTO open_banking_transactions
         VALUES ($OpenBankingTransactionCodec)
         """.command

  val getTransactionsByCompanyUuid: Query[CompanyUuid, OpenBankingTransaction] =
    sql"""
         SELECT * FROM open_banking_transactions
         WHERE company_uuid = $companyUuidCodec
         """.query(OpenBankingTransactionCodec)

end OpenBankingTransactionsSQL
