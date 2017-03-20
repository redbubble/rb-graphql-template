package com.redbubble.gql.backends.locales

import com.redbubble.gql.services.locale.Locale.DefaultLanguage
import com.redbubble.gql.services.locale._
import com.redbubble.util.json.syntax._
import io.circe.Decoder

trait LocalesDecoders {
  final val countryDecoder: Decoder[Country] = Decoder.instance { c =>
    for {
      code <- c.downField("code").as[String].map(IsoCountryCode)
      name <- c.downField("name").as[String].map(CountryName)
    } yield Country(code, Some(name))
  }

  final val currencyDecoder: Decoder[Currency] = Decoder.instance { c =>
    for {
      code <- c.downField("code").as[String].map(IsoCurrencyCode)
      name <- c.downField("name").as[String].map(CurrencyName)
      prefix <- c.downField("prefix").as[String].map(CurrencyPrefix)
      unit <- c.downField("unit").as[String].map(CurrencyUnit)
    } yield Currency(code, Some(name), Some(prefix), Some(unit))
  }

  final val languageDecoder: Decoder[Language] = Decoder.instance { c =>
    for {
      code <- c.downField("code").as[String].map(IsoLanguageCode)
      name <- c.downField("name").as[String].map(LanguageName)
    } yield Language(code, Some(name))
  }

  final val localeDecoder: Decoder[Locale] = Decoder.instance { c =>
    for {
      country <- c.downField("data").downField("country").as[Country](countryDecoder)
      currency <- c.downField("data").downField("currency").as[Currency](currencyDecoder)
    } yield Locale(country, currency, DefaultLanguage)
  }

  final val localeInformationDecoder: Decoder[LocaleInformation] = Decoder.instance { c =>
    for {
      countries <- c.downField("data").downField("countries").as[Seq[Country]](countryDecoder.seqDecoder)
      currencies <- c.downField("data").downField("currencies").as[Seq[Currency]](currencyDecoder.seqDecoder)
      languages <- c.downField("data").downField("languages").as[Seq[Language]](languageDecoder.seqDecoder)
    } yield LocaleInformation(countries, currencies, languages)
  }
}

object LocalesDecoders extends LocalesDecoders
