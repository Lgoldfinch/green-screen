package com.green.screen.analytics.engine.programs

import com.green.screen.analytics.engine.algebras.Companies
import com.green.screen.analytics.engine.algebras.UserTransactions
import com.green.screen.analytics.engine.domain.transactions.{CreateTransactionRequest, TransactionEntity}

class ProcessTransaction[F[_]](companies: Companies[F], transactions: UserTransactions[F]):
  def run(request: CreateTransactionRequest): F[Unit] = ???
  
end ProcessTransaction

