package com.green.screen.banking.routes

import cats.effect.Concurrent
import cats.syntax.all.*
import com.green.screen.banking.domain.companies.CreateCompanyRequest
import com.green.screen.banking.programs.CreateCompany
import com.green.screen.common.auth.UserType
import org.http4s.*
import org.http4s.circe.CirceEntityCodec.circeEntityDecoder
import org.http4s.dsl.Http4sDsl
import org.typelevel.log4cats.Logger

object CompanyRoutes:
  def routes[F[_]](
      createCompany: CreateCompany[F]
  )(using Concurrent[F], Logger[F]): AuthedRoutes[UserType, F] = {
    val dsl = new Http4sDsl[F] {}
    import dsl.*

    AuthedRoutes.of {
      case authReq @ POST -> Root / "companies" as _ =>
        authReq.req
          .as[CreateCompanyRequest]
          .flatMap(createCompany.createCompany) >> Created()
    }
  }
end CompanyRoutes