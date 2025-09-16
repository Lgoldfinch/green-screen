package com.green.screen.banking.programs

import cats.data.NonEmptyList
import com.green.screen.banking.algebras.{Companies, Users}
import com.green.screen.banking.domain.companies.{Company, CompanyName, CompanyUuid}
import com.green.screen.banking.domain.users.*

class TestUsers[F[_]] extends Users[F] {
    override def createUser(user: User): F[Unit] = ???

    override def getScore(userUuid: UserUuid): F[UserScore] = ???

    override def getUser(userUuid: UserUuid): F[Option[User]] = ???
  }

class TestCompanies[F[_]] extends Companies[F] {
  override def createCompany(company: Company): F[Unit] = ???

  override def createCompanies(companies: NonEmptyList[Company]): F[Unit] = ???

  override def getCompany(companyUuid: CompanyUuid): F[Option[Company]] = ???

  override def getCompanyUuidByName(transactionEntity: CompanyName): F[Option[CompanyUuid]] = ???
}