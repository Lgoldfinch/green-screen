package com.green.screen.banking.domain

import cats.syntax.all.*
import cats.{Eq, Show}
import com.green.screen.common.db.*
import eu.timepit.refined.types.string.NonEmptyString
import io.circe.derivation.Configuration.default
import io.circe.derivation.{Configuration, ConfiguredDecoder}
import io.circe.refined.*
import skunk.*
import skunk.codec.all.*

import java.util.UUID

object companies {

  given configuration: Configuration = default.withPascalCaseMemberNames

  opaque type CompanyUuid = UUID

  object CompanyUuid {
    def apply(uuid: UUID): CompanyUuid = uuid

    extension (cUuid: CompanyUuid) def value: CompanyUuid = cUuid

    given Ordering[CompanyUuid] = _.compareTo(_)

    given Eq[CompanyUuid] = Eq.fromUniversalEquals
  }

  val companyUuidCodec: Codec[CompanyUuid] = uuid.imap(CompanyUuid.apply)(CompanyUuid.value)

  opaque type CompanyName = NonEmptyString

  object CompanyName {
    def apply(nes: NonEmptyString): CompanyName = nes

    extension (cn: CompanyName) {
      def value: NonEmptyString = cn
    }
  }

  val companyNameCodec: Codec[CompanyName] = nesCodec.imap(CompanyName.apply)(CompanyName.value)

  opaque type CompanyCo2EmissionsMetricTonnes = Double

  object CompanyCo2EmissionsMetricTonnes {
    def apply(f: Double): CompanyCo2EmissionsMetricTonnes = f

    extension (co2: CompanyCo2EmissionsMetricTonnes) def value: Double = co2
  }

  val co2EmissionsCodec: Codec[CompanyCo2EmissionsMetricTonnes] =
    float8.imap(CompanyCo2EmissionsMetricTonnes.apply)(CompanyCo2EmissionsMetricTonnes.value)

  final case class Company(uuid: CompanyUuid, name: CompanyName, co2Emissions: Option[CompanyCo2EmissionsMetricTonnes])

  object Company {
    given Show[Company] = Show.show(_.toString)
  }

  val companyCodec: Codec[Company] =
    (companyUuidCodec, companyNameCodec, co2EmissionsCodec.opt).tupled.imap(Company.apply) {
      case Company(uuid, name, co2Emissions) => (uuid, name, co2Emissions)
    }

  final case class CreateCompanyRequest(name: CompanyName, co2Emissions: Option[CompanyCo2EmissionsMetricTonnes]) derives ConfiguredDecoder
}
