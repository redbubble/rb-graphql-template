package com.redbubble.gql.services.locale

import com.redbubble.gql.services.locale.Currency.currencyFromCodeDecoder
import com.redbubble.gql.util.spec.ModelGenerators
import com.redbubble.util.spec.SpecHelper
import org.scalacheck.Prop._
import org.scalacheck.Properties
import org.specs2.mutable.Specification

final class CurrencySpec extends Specification with SpecHelper with ModelGenerators {
  val currencyDecodingProp = new Properties("Decoding currencies") {
    property("valid currencies") = forAll(genIsoCurrencyCode) { (code: IsoCurrencyCode) =>
      val decoded = decode(buildJson(code))(currencyFromCodeDecoder)
      decoded must beRight(Currency(code, None, None, None))
    }
  }

  s2"Currency strings can be decoded$currencyDecodingProp"

  private def buildJson(s: String): String = s""""$s""""
}
