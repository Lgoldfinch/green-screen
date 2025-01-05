package com.green.screen.analytics.engine.algebras

import cats.effect.kernel.{ Concurrent, MonadCancelThrow, Resource }
import cats.syntax.all.*
import com.green.screen.analytics.engine.*
import com.green.screen.analytics.engine.algebras.TransactionsSQL.*
import com.green.screen.analytics.engine.domain.*
import com.green.screen.analytics.engine.domain.CompanyUuid.companyUuidCodec
import com.green.screen.analytics.engine.domain.OpenAPITransaction.openAPITransactionCodec
import org.typelevel.log4cats.Logger
import skunk.*
import skunk.syntax.all.*

trait OpenAPITransactions[F[_]] {
  def createTransaction(transaction: OpenAPITransaction): F[Unit]
  def getTransactions(companyUuid: CompanyUuid): F[List[OpenAPITransaction]]
}

object OpenAPITransactions:
  def make[F[_]: MonadCancelThrow: Concurrent: Logger](resource: Resource[F, Session[F]]): OpenAPITransactions[F] =
    new OpenAPITransactions[F]:
      override def createTransaction(transaction: OpenAPITransaction): F[Unit] = resource.use(session =>
        for {
          command <- session.prepare(insertTransaction)
          _       <- command.execute(transaction)
          _       <- Logger[F].info(s"Inserted transaction ${transaction.uuid} for company ${transaction.companyUuid}")
        } yield ()
      )

      override def getTransactions(companyUuid: CompanyUuid): F[List[OpenAPITransaction]] = {
        resource.use(session =>
          for {
            query        <- session.prepare(getTransactionsByCompanyUuid)
            transactions <- query.stream(companyUuid, 32).compile.toList
          } yield transactions
        )
      }

end OpenAPITransactions

object TransactionsSQL:
  val insertTransaction: Command[OpenAPITransaction] =
    sql"""
         INSERT INTO open_api_transactions
         VALUES ($openAPITransactionCodec)
         """.command

  val getTransactionsByCompanyUuid: Query[CompanyUuid, OpenAPITransaction] =
    sql"""
         SELECT * FROM open_api_transactions
         WHERE company_uuid = $companyUuidCodec
         """.query(openAPITransactionCodec)

end TransactionsSQL
