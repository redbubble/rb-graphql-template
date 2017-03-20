package com.redbubble.gql.backends.locales

import com.redbubble.gql.services.locale.{Country, Currency, Locale, _}

trait LocalesBackendJson {
  def localeJson(locale: Locale): String =
    s"""
       |{
       |  "data": {
       |    "country": ${countryJson(locale.country)},
       |    "currency": ${currencyJson(locale.currency)}
       |  }
       |}
     """.stripMargin

  final def countryJson(country: Country): String =
    s"""
       |{
       |  "code": "${country.isoCode}",
       |  "name": "${country.name.getOrElse("British Indian Ocean Territory")}"
       |}
     """.stripMargin

  final def currencyJson(currency: Currency): String =
    s"""
       |{
       |  "code": "${currency.isoCode}",
       |  "unit": "${currency.unit.getOrElse("$")}",
       |  "prefix": "${currency.prefix.getOrElse("US")}",
       |  "name": "${currency.name.getOrElse("United States Dollar")}"
       |}
    """.stripMargin

  final def languageJson(language: Language): String =
    s"""
       |{
       |  "code": "${language.isoCode}",
       |  "name": "${language.name.getOrElse("Français")}"
       |}
    """.stripMargin

  final def localeInformationJson(localeInformation: LocaleInformation): String =
    s"""
       |{
       |  "data": {
       |    "languages": [
       |      ${localeInformation.languages.map(languageJson).mkString(",")}
       |    ],
       |    "currencies": [
       |      ${localeInformation.currencies.map(currencyJson).mkString(",")}
       |    ],
       |    "countries": [
       |      ${localeInformation.countries.map(countryJson).mkString(",")}
       |    ]
       |  }
       |}
     """.stripMargin
}
