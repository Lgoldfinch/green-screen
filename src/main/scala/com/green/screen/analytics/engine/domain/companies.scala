package com.green.screen.analytics.engine.domain

import cats.syntax.all.*
import cats.{ Eq, Show }
import com.green.screen.analytics.engine.domain.*
import com.green.screen.analytics.engine.domain.CompanyCo2EmissionsMetricTonnes.co2EmissionsCodec
import com.green.screen.analytics.engine.domain.CompanyName.*
import com.green.screen.analytics.engine.domain.CompanyUuid.*
import com.green.screen.common.domain.skunks.*
import eu.timepit.refined.types.string.NonEmptyString
import skunk.*
import skunk.codec.all.*

import java.util.UUID

opaque type CompanyUuid = UUID

object CompanyUuid {
  def apply(uuid: UUID): CompanyUuid = uuid

  extension (cUuid: CompanyUuid) def value: CompanyUuid = cUuid

  val companyUuidCodec: Codec[CompanyUuid]     = uuid.imap(CompanyUuid.apply)(_.value)
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

  implicit val companyShow: Show[Company] = Show.show(_.toString)
}
