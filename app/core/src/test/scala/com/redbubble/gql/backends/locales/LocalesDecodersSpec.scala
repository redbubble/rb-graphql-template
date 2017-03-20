package com.redbubble.gql.backends.locales

import com.redbubble.gql.services.locale.Locale.DefaultLanguage
import com.redbubble.gql.services.locale.{Locale, LocaleInformation}
import com.redbubble.gql.util.spec.ModelGenerators
import com.redbubble.util.spec.SpecHelper
import org.scalacheck.Prop.forAll
import org.scalacheck.Properties
import org.specs2.mutable.Specification

final class LocalesDecodersSpec extends Specification with SpecHelper with ModelGenerators with LocalesBackendJson {
  val localeDecodingProp = new Properties("Locale decoding") {
    property("decoding") = forAll(genLocale) { (locale: Locale) =>

      // we hard code the language as we don't get it when we parse the locale for an IP
      val localeWithLanguage = locale.copy(language = DefaultLanguage)

      val decoded = decode(localeJson(localeWithLanguage))(LocalesDecoders.localeDecoder)
      decoded must beRight(localeWithLanguage)
    }
  }

  s2"Decoding locales$localeDecodingProp"

  val localeInfoDecodingProp = new Properties("Locale information decoding") {
    property("decoding") = forAll(genLocaleInformation) { (info: LocaleInformation) =>
      val decoded = decode(localeInformationJson(info))(LocalesDecoders.localeInformationDecoder)
      decoded must beRight(info)
    }
  }

  s2"Decoding locale information$localeInfoDecodingProp"
}
