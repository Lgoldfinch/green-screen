package com.green.screen.analytics.engine.algebras

import cats.effect.kernel.{ Concurrent, MonadCancelThrow, Resource }
import cats.syntax.all.*
import com.green.screen.analytics.engine.*
import com.green.screen.analytics.engine.algebras.TransactionsSQL.*
import com.green.screen.analytics.engine.domain.*
import com.green.screen.analytics.engine.domain.CompanyUuid.companyUuidCodec
import com.green.screen.analytics.engine.domain.UserTransaction.transactionCodec
import org.typelevel.log4cats.Logger
import skunk.*
import skunk.syntax.all.*

trait UserTransactions[F[_]] {
  def createTransaction(transaction: UserTransaction): F[Unit]
  def getTransactions(companyUuid: CompanyUuid): F[List[UserTransaction]]
}

object UserTransactions:
  def make[F[_]: MonadCancelThrow: Concurrent: Logger](resource: Resource[F, Session[F]]): UserTransactions[F] =
    new UserTransactions[F]:
      override def createTransaction(transaction: UserTransaction): F[Unit] = resource.use(session =>
        for {
          command <- session.prepare(insertTransaction)
          _       <- command.execute(transaction)
          _       <- Logger[F].info(s"Inserted transaction ${transaction.uuid} for company ${transaction.companyUuid}")
        } yield ()
      )

      override def getTransactions(companyUuid: CompanyUuid): F[List[UserTransaction]] = {
        resource.use(session =>
          for {
            query        <- session.prepare(getTransactionsByCompanyUuid)
            transactions <- query.stream(companyUuid, 32).compile.toList
          } yield transactions
        )
      }

end UserTransactions

object TransactionsSQL:
  val insertTransaction: Command[UserTransaction] =
    sql"""
         INSERT INTO transactions
         VALUES ($transactionCodec)
         """.command

  val getTransactionsByCompanyUuid: Query[CompanyUuid, UserTransaction] =
    sql"""
         SELECT * FROM transactions
         WHERE company_uuid = $companyUuidCodec
         """.query(transactionCodec)

end TransactionsSQL
