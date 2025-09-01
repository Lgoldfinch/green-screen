package com.green.screen.banking.domain

import cats.syntax.all.*
import cats.{ Eq, Show }
import com.green.screen.banking.domain.*
import CompanyCo2EmissionsMetricTonnes.co2EmissionsCodec
import CompanyName.*
import CompanyUuid.*
import com.green.screen.common.db.*
import eu.timepit.refined.types.string.NonEmptyString
import skunk.*
import skunk.codec.all.*

import java.util.UUID

opaque type CompanyUuid = UUID

object CompanyUuid {
  def apply(uuid: UUID): CompanyUuid = uuid

  extension (cUuid: CompanyUuid) def value: CompanyUuid = cUuid

  val companyUuidCodec: Codec[CompanyUuid] = uuid.imap(CompanyUuid.apply)(_.value)
  given Ordering[CompanyUuid]              = _.compareTo(_)
  given Eq[CompanyUuid]                    = Eq.fromUniversalEquals

}

opaque type CompanyName = NonEmptyString

object CompanyName {
  def apply(nes: NonEmptyString): CompanyName = nes

  extension (cn: CompanyName) {
    def value: NonEmptyString = cn
  }

  val companyNameCodec: Codec[CompanyName] = nesCodec.imap(CompanyName.apply)(_.value)
}

opaque type CompanyCo2EmissionsMetricTonnes = Double

object CompanyCo2EmissionsMetricTonnes {
  def apply(f: Double): CompanyCo2EmissionsMetricTonnes = f

  extension (co2: CompanyCo2EmissionsMetricTonnes) def value: Double = co2

  val co2EmissionsCodec: Codec[CompanyCo2EmissionsMetricTonnes] =
    float8.imap(CompanyCo2EmissionsMetricTonnes.apply)(_.value)
}

final case class Company(uuid: CompanyUuid, name: CompanyName, co2Emissions: CompanyCo2EmissionsMetricTonnes)

object Company {
  val companyCodec: Codec[Company] =
    (companyUuidCodec, companyNameCodec, co2EmissionsCodec).tupled.imap(Company.apply) {
      case Company(uuid, name, co2Emissions) => (uuid, name, co2Emissions)
    }

  given Show[Company] = Show.show(_.toString)
}
