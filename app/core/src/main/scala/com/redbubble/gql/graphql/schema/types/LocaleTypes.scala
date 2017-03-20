package com.redbubble.gql.graphql.schema.types

import java.net.InetAddress

import cats.syntax.either._
import com.redbubble.graphql.ScalarTypes.stringScalarType
import com.redbubble.gql.services.locale.Locale.{DefaultCountry, DefaultCurrency, DefaultLanguage}
import com.redbubble.gql.services.locale._
import sangria.marshalling.ToInput.ScalarToInput
import sangria.schema._
import sangria.validation.ValueCoercionViolation

object LocaleTypes {

  //
  // Country Code
  //

  private case object CountryCodeCoercionViolation extends ValueCoercionViolation(s"ISO country code expected.")

  private def parseCountryCode(c: String): Either[CountryCodeCoercionViolation.type, IsoCountryCode] =
    Right(IsoCountryCode(c.toUpperCase))

  val CountryCodeType: ScalarType[IsoCountryCode] = stringScalarType(
    "CountryCode",
    s"An ISO country code; used for regional pricing & localised strings, e.g. ${DefaultCountry.isoCode}.",
    parseCountryCode, () => CountryCodeCoercionViolation
  )

  val CountryCodeArg: Argument[IsoCountryCode] = Argument(
    "country", CountryCodeType,
    description = s"An ISO country code; used for regional pricing & localised strings, e.g. ${DefaultCountry.isoCode}."
  )

  //
  // Country
  //

  val CountryType: ObjectType[Unit, Country] = ObjectType(
    "Country", "A country, used for regional pricing & localised strings.",
    fields[Unit, Country](
      Field(
        name = "code",
        fieldType = CountryCodeType,
        description = Some(s"The ISO code of the country, e.g. ${DefaultCountry.isoCode}."),
        resolve = _.value.isoCode
      ),
      Field(
        name = "name",
        fieldType = OptionType(StringType),
        description = Some(s"The name of the country, e.g. ${DefaultCountry.name.getOrElse("United States")}."),
        resolve = _.value.name
      )
    )
  )

  //
  // Currency Code
  //

  private case object CurrencyCodeCoercionViolation extends ValueCoercionViolation(s"ISO currency code expected.")

  private def parseCurrencyCode(c: String): Either[CurrencyCodeCoercionViolation.type, IsoCurrencyCode] =
    Right(IsoCurrencyCode(c.toUpperCase))

  val CurrencyCodeType: ScalarType[IsoCurrencyCode] = stringScalarType(
    "CurrencyCode",
    s"An ISO currency code, e.g. ${DefaultCurrency.isoCode}.",
    parseCurrencyCode, () => CurrencyCodeCoercionViolation
  )

  val CurrencyCodeArg: Argument[IsoCurrencyCode] = Argument(
    "currency", CurrencyCodeType,
    description = s"An ISO currency code, e.g. ${DefaultCurrency.isoCode}."
  )

  //
  // Currency
  //

  val CurrencyType: ObjectType[Unit, Currency] = ObjectType(
    "Currency", "A currency.",
    fields[Unit, Currency](
      Field(
        name = "code",
        fieldType = CurrencyCodeType,
        description = Some(s"The ISO code of this currency, e.g. ${DefaultCurrency.isoCode}."),
        resolve = _.value.isoCode
      ),
      Field(
        name = "name",
        fieldType = OptionType(StringType),
        description = Some(s"The display name of the currency, e.g. ${DefaultCurrency.name.getOrElse("United States Dollar")}."),
        resolve = _.value.name
      ),
      Field(
        name = "prefix",
        fieldType = OptionType(StringType),
        description = Some(s"A short code that preceeds the currency unit, e.g. ${DefaultCurrency.prefix.getOrElse("US")}."),
        resolve = _.value.prefix
      ),
      Field(
        name = "unit",
        fieldType = OptionType(StringType),
        description = Some(s"The currency unit/symbol, e.g. ${DefaultCurrency.unit.getOrElse("$")}."),
        resolve = _.value.unit
      )
    )
  )

  //
  // Language
  //

  private implicit val languageInput = new ScalarToInput[IsoLanguageCode]

  private case object LanguageCodeCoercionViolation extends ValueCoercionViolation(s"ISO language code expected.")

  private def parseLanguage(s: String): Either[LanguageCodeCoercionViolation.type, IsoLanguageCode] =
    Right(IsoLanguageCode(s.toLowerCase))

  val LanguageCodeType: ScalarType[IsoLanguageCode] = stringScalarType(
    "LanguageCode", s"An ISO language code, e.g. ${DefaultLanguage.isoCode}.",
    parseLanguage, () => LanguageCodeCoercionViolation
  )

  val LanguageCodeArg: Argument[IsoLanguageCode] = Argument(
    name = "language",
    argumentType = OptionInputType(LanguageCodeType),
    defaultValue = DefaultLanguage.isoCode,
    description = s"An ISO language code, default ${DefaultLanguage.isoCode}."
  )

  //
  // Language
  //

  val LanguageType: ObjectType[Unit, Language] = ObjectType(
    "Language", "A language.",
    fields[Unit, Language](
      Field(
        name = "code",
        fieldType = LanguageCodeType,
        description = Some("The ISO country code of the language."),
        resolve = _.value.isoCode
      ),
      Field(
        name = "name",
        fieldType = OptionType(StringType),
        description = Some("The display name of the language."),
        resolve = _.value.name
      )
    )
  )

  //
  // IP address
  //

  private implicit val inetAddressInput = new ScalarToInput[InetAddress]

  private case object InetAddressCoercionViolation extends ValueCoercionViolation(s"Valid IP address value expected.")

  private def parseInetAddress(s: String): Either[InetAddressCoercionViolation.type, InetAddress] =
    Either.catchNonFatal(InetAddress.getByName(s)).left.map(_ => InetAddressCoercionViolation)

  val InetAddressType: ScalarType[InetAddress] = stringScalarType(
    "InetAddress", s"Internet Protocol (IP) address.",
    parseInetAddress, () => InetAddressCoercionViolation)

  val InetAddressArg: Argument[InetAddress] = Argument(
    name = "ipAddress",
    argumentType = InetAddressType,
    description = "The IP address of the client iOS app."
  )

  val LocaleType: ObjectType[Unit, Locale] = ObjectType(
    "Locale", "Locale information (country, language, currency) for a device based on its IP address.",
    fields[Unit, Locale](
      Field(
        name = "country",
        fieldType = CountryType,
        description = Some(s"The country of this locale, used for regional pricing & localised strings."),
        resolve = _.value.country
      ),
      Field(
        name = "currency",
        fieldType = CurrencyType,
        description = Some(s"The currency for this locale."),
        resolve = _.value.currency
      ),
      Field(
        name = "language",
        fieldType = LanguageType,
        description = Some(s"An ISO language code."),
        resolve = _.value.language
      )
    )
  )

  val LocaleInformationType: ObjectType[Unit, LocaleInformation] = ObjectType(
    "LocaleInformation", "All supported countries, currencies & langauges.",
    fields[Unit, LocaleInformation](
      Field(
        name = "countries",
        fieldType = ListType(CountryType),
        description = Some("All supported countries."),
        resolve = _.value.countries
      ),
      Field(
        name = "currencies",
        fieldType = ListType(CurrencyType),
        description = Some("All supported currencies."),
        resolve = _.value.currencies
      ),
      Field(
        name = "languages",
        fieldType = ListType(LanguageType),
        description = Some("All supported langauges."),
        resolve = _.value.languages
      )
    )
  )
}
