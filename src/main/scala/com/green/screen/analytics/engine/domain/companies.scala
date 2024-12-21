package com.green.screen.analytics.engine.domain

import cats.{Eq, Show}
import cats.syntax.all.*
import com.green.screen.analytics.engine.domain.companies.*
import com.green.screen.analytics.engine.domain.companies.CompanyCo2EmissionsMetricTonnes.*
import com.green.screen.analytics.engine.domain.companies.CompanyName.*
import com.green.screen.analytics.engine.domain.companies.CompanyUuid.*
import com.green.screen.common.domain.skunks.*
import eu.timepit.refined.types.string.NonEmptyString
import skunk.*
import skunk.codec.all.*

import java.util.UUID

object companies:
  opaque type CompanyUuid = UUID

  object CompanyUuid {
    def apply(uuid: UUID): CompanyUuid = uuid

    extension (cUuid: CompanyUuid)
      def value: CompanyUuid = cUuid

    val companyUuidCodec: Codec[CompanyUuid] = uuid.imap(CompanyUuid.apply)(_.value)
    implicit val ordering: Ordering[CompanyUuid] = _.compareTo(_)
    
    implicit val eq: Eq[CompanyUuid] = Eq.fromUniversalEquals
    
  }

  opaque type CompanyName = NonEmptyString


  object CompanyName {
    def apply(nes: NonEmptyString): CompanyName = nes

    extension (cn: CompanyName) {
      def value: NonEmptyString = cn
    }

    val companyNameCodec: Codec[CompanyName] = nesCodec.imap(CompanyName.apply)(_.value)
  }

  opaque type CompanyCo2EmissionsMetricTonnes = Float

  object CompanyCo2EmissionsMetricTonnes {
    def apply(f: Float): CompanyCo2EmissionsMetricTonnes = f

    extension (co2: CompanyCo2EmissionsMetricTonnes)
      def value: Float = co2

    val co2EmissionsCodec: Codec[CompanyCo2EmissionsMetricTonnes] = float4.imap(CompanyCo2EmissionsMetricTonnes.apply)(_.value)
  }


  final case class Company(uuid: CompanyUuid, name: CompanyName, co2Emissions: CompanyCo2EmissionsMetricTonnes)

  object Company {
    val companyCodec: Codec[Company] =
      (companyUuidCodec, companyNameCodec, co2EmissionsCodec).tupled.imap(Company.apply) {
        case Company(uuid, name, co2Emissions) => (uuid, name, co2Emissions)
      }
    
    implicit val companyShow: Show[Company] = Show.show(_.toString)
  }
end companies

