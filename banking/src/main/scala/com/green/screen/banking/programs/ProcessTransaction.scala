package com.green.screen.banking.programs

import cats.MonadThrow
import com.green.screen.banking.domain.*
import cats.syntax.all.*
import com.green.screen.banking.domain.TransactionEntity.*
import ProcessTransaction.CompanyNotFound
import com.green.screen.banking.algebras.{ Companies, OpenAPITransactions }
import com.green.screen.common.effects.GenUUID
import com.green.screen.common.misc.CreatedAt

import java.util.UUID
import scala.util.control.NoStackTrace

class ProcessTransaction[F[_]: MonadThrow: GenUUID](companies: Companies[F], transactions: OpenAPITransactions[F]):
  def createTransaction(request: CreateTransactionRequest): F[Unit] = {

    val companyName = request.name.toCompanyName

    for {
      companyUuidOpt  <- companies.getCompanyUuidByName(companyName)
      companyUuid     <- companyUuidOpt.liftTo[F](CompanyNotFound(companyName))
      transactionUuid <- GenUUID[F].make.map(TransactionUuid.apply)
      transaction = OpenAPITransaction(
        transactionUuid,
        companyUuid,
        request.userUuid,
        request.amount,
        CreatedAt.now
      )
      _ <- transactions.createTransaction(transaction)
    } yield ()
  }

end ProcessTransaction

object ProcessTransaction:
  case class CompanyNotFound(companyName: CompanyName) extends NoStackTrace {
    override def getMessage: String = s"Could not find Company: ${companyName.value}"
  }
end ProcessTransaction
