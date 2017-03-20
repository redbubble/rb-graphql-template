package com.redbubble.gql.services.locale

import com.redbubble.gql.services.locale.Country.countryFromCodeDecoder
import com.redbubble.gql.util.spec.ModelGenerators
import com.redbubble.util.spec.SpecHelper
import org.scalacheck.Prop._
import org.scalacheck.Properties
import org.specs2.mutable.Specification

final class CountrySpec extends Specification with SpecHelper with ModelGenerators {
  val countryDecodingProp = new Properties("Decoding countries") {
    property("valid countries") = forAll(genIsoAlpha2CountryCode) { (code: IsoCountryCode) =>
      val decoded = decode(buildJson(code))(countryFromCodeDecoder)
      decoded must beRight(Country(code, None))
    }
  }

  s2"Country strings can be decoded$countryDecodingProp"

  private def buildJson(s: String): String = s""""$s""""
}
