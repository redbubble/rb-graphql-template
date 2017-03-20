package com.redbubble.gql.util.spec

import com.redbubble.gql.backends._
import com.redbubble.gql.backends.search.{RawSearchResult, ResultMetadata}
import com.redbubble.gql.core._
import com.redbubble.gql.magickraum._
import com.redbubble.gql.product._
import com.redbubble.gql.product.config.ProductConfiguration.configItemAsFlatConfig
import com.redbubble.gql.product.config._
import com.redbubble.gql.services.artists._
import com.redbubble.gql.services.cart.{CartProduct, Quantity}
import com.redbubble.gql.services.locale._
import com.redbubble.gql.services.product._
import com.redbubble.gql.services.search._
import com.redbubble.util.spec.gen.{GenHelpers, JsonGenerators, StdLibGenerators}
import org.scalacheck.{Arbitrary, Gen}

trait ModelGenerators extends StdLibGenerators with JsonGenerators with GenHelpers with MagickraumHelper {
  val genIsoAlpha2CountryCode: Gen[IsoCountryCode] = genNonEmptyString.map(IsoCountryCode)
  val genCountryName: Gen[CountryName] = genNonEmptyString.map(CountryName)

  val genCountry: Gen[Country] = for {
    code <- genIsoAlpha2CountryCode
    name <- genCountryName
  } yield Country(code, Some(name))

  val genCountryJustCode: Gen[Country] = for {
    code <- genIsoAlpha2CountryCode
  } yield Country(code, None)

  val genIsoCurrencyCode: Gen[IsoCurrencyCode] = genNonEmptyString.map(IsoCurrencyCode)
  val genCurrencyName: Gen[CurrencyName] = genNonEmptyString.map(CurrencyName)
  val genCurrencyPrefix: Gen[CurrencyPrefix] = genNonEmptyString.map(CurrencyPrefix)
  val genCurrencyUnit: Gen[CurrencyUnit] = genNonEmptyString.map(CurrencyUnit)

  val genTopic: Gen[Topic] = genNonEmptyString.map(Topic)

  val genCurrency: Gen[Currency] = for {
    code <- genIsoCurrencyCode
    name <- genCurrencyName
    prefix <- genCurrencyPrefix
    unit <- genCurrencyUnit
  } yield Currency(code, Some(name), Some(prefix), Some(unit))

  val genCurrencyJustCode: Gen[Currency] = for {
    code <- genIsoCurrencyCode
  } yield Currency(code, None, None, None)

  val genIsoLanguageCode: Gen[IsoLanguageCode] = genNonEmptyString.map(IsoLanguageCode)
  val genLanguageName: Gen[LanguageName] = genNonEmptyString.map(LanguageName)

  val genLanguage: Gen[Language] = for {
    code <- genIsoLanguageCode
    name <- genLanguageName
  } yield Language(code, Some(name))

  val genLanguageJustCode: Gen[Language] = for {
    code <- genIsoLanguageCode
    name <- genLanguageName
  } yield Language(code, Some(name))

  val genLocale: Gen[Locale] = for {
    country <- genCountry
    currency <- genCurrency
    language <- genLanguage
  } yield Locale(country, currency, language)

  val genLocaleJustCodes: Gen[Locale] = for {
    country <- genCountryJustCode
    currency <- genCurrencyJustCode
    language <- genLanguageJustCode
  } yield Locale(country, currency, language)

  val genLocaleInformation: Gen[LocaleInformation] = for {
    country <- genCountry
    currency <- genCurrency
    language <- genLanguage
  } yield LocaleInformation(Seq(country), Seq(currency), Seq(language))

  val genArtist: Gen[Artist] = for {
    id <- genPositiveInt.map(i => Id(i.toString))
    username <- genNonEmptyString.map(Username)
    name <- genNonEmptyString.map(ArtistName)
    avatar <- genUrl
    location <- genNonEmptyString.map(Location)
  } yield Artist(Some(id), username, name, avatar, Some(location))

  implicit val arbArtist = Arbitrary(genArtist)

  val genPrice: Gen[Price] = for {
    amount <- Gen.oneOf(BigDecimal(1.0), BigDecimal(10.0), BigDecimal(100.15))
    currency <- genCurrencyJustCode
  } yield Price(amount, currency)

  implicit val arbPrice = Arbitrary(genPrice)

  val genSupportedProduct: Gen[SupportedProduct] = Gen.oneOf(SupportedProduct.supportedProducts)

  val genProductTypeId: Gen[ProductTypeId] = genSupportedProduct.map(_.typeId)
  val genProductTypeName: Gen[ProductDisplayName] = genNonEmptyString.map(ProductDisplayName)

  val genProductDefinition: Gen[ProductDefinition] = for {
    typeId <- genProductTypeId
    typeName <- genProductTypeName
  } yield ProductDefinition(typeId, typeName)

  implicit val arbProductDefinition = Arbitrary(genProductDefinition)

  val genConfigItemName: Gen[ConfigItemName] = genNonEmptyString.map(ConfigItemName)
  val genConfigItemLabel: Gen[ConfigItemLabel] = genNonEmptyString.map(ConfigItemLabel)
  val genConfigItemValue: Gen[ConfigItemValue] = genNonEmptyString.map(ConfigItemValue)
  val genConfigItemDisplayName: Gen[ConfigItemDisplayName] = genNonEmptyString.map(ConfigItemDisplayName)

  // We associate size display names with measurement labels, to find a matching measurement.
  def genApparelSizeMeasurement(name: ConfigItemDisplayName): Gen[ApparelSizeMeasurement] = for {
    chest <- genNonEmptyString.map(MeasurementChest)
    length <- genNonEmptyString.map(MeasurementLength)
  } yield ApparelSizeMeasurement(MeasurementLabel(name), chest, length)

  val genProductSize: Gen[ProductSize] = for {
    value <- genConfigItemValue
    displayName <- genConfigItemDisplayName
    selected <- Arbitrary.arbBool.arbitrary
  } yield ProductSize(value, displayName, None, selected)

  val genApparelSize: Gen[ProductSize] = for {
    value <- genConfigItemValue
    displayName <- genConfigItemDisplayName
    selected <- Arbitrary.arbBool.arbitrary
    measurement <- genApparelSizeMeasurement(displayName)
  } yield ProductSize(value, displayName, Some(measurement), selected)

  val genPhoneModel: Gen[PhoneModel] = for {
    value <- genConfigItemValue
    displayName <- genConfigItemDisplayName
    selected <- Arbitrary.arbBool.arbitrary
  } yield PhoneModel(value, displayName, selected)

  val genPhoneCover: Gen[PhoneCoverType] = for {
    value <- genConfigItemValue
    displayName <- genConfigItemDisplayName
    selected <- Arbitrary.arbBool.arbitrary
  } yield PhoneCoverType(value, displayName, selected)

  val genColourHex: Gen[ColourHex] = genNonEmptyString.map(ColourHex)

  // Note. For some reason this doesn't work as a for comprehension
  val genApparelColour: Gen[ApparelColour] = genConfigItemValue.flatMap { value =>
    genConfigItemDisplayName.flatMap { displayName =>
      genColourHex.flatMap { hexValue =>
        Arbitrary.arbBool.arbitrary.flatMap { selected =>
          ApparelColour(value, displayName, hexValue, selected)
        }
      }
    }
  }

  implicit val arbApparelColour = Arbitrary(genApparelColour)

  // Note. For some reason this doesn't work as a for comprehension
  val genFrameColour: Gen[FrameColour] = genConfigItemValue.flatMap { value =>
    genConfigItemDisplayName.flatMap { displayName =>
      Arbitrary.arbBool.arbitrary.flatMap { selected =>
        FrameColour(value, displayName, selected)
      }
    }
  }

  implicit val arbFrameColour = Arbitrary(genFrameColour)

  // Note. For some reason this doesn't work as a for comprehension
  val genMatteColour: Gen[MatteColour] = genConfigItemValue.flatMap { value =>
    genConfigItemDisplayName.flatMap { displayName =>
      Arbitrary.arbBool.arbitrary.flatMap { selected =>
        MatteColour(value, displayName, selected)
      }
    }
  }

  implicit val arbMatteColour = Arbitrary(genMatteColour)

  val genExtraConfig: Gen[FlattenedConfigItem] = for {
    name <- genNonEmptyString.map(ConfigItemName)
    label <- genNonEmptyString.map(ConfigItemLabel)
    value <- genNonEmptyString.map(ConfigItemValue)
    displayName <- genNonEmptyString.map(ConfigItemDisplayName)
    selected <- Arbitrary.arbBool.arbitrary
  } yield FlattenedConfigItem(name, label, value, displayName, selected)

  val genNonDisplayableConfig: Gen[FlattenedConfigItem] = for {
    name <- genNonEmptyString.map(ConfigItemName)
    value <- genNonEmptyString.map(ConfigItemValue)
  } yield FlattenedConfigItem(name, ConfigItemLabel(name), value, ConfigItemDisplayName(name), defaultSelection = false)

  val genApparelConfig: Gen[ApparelConfig] = for {
    size <- genApparelSize
    colour <- genApparelColour
    nonDisplay <- genNonDisplayableConfig
  } yield ApparelConfig(Seq(size), Seq(colour), Seq(nonDisplay))

  implicit val arbApparelConfig = Arbitrary(genApparelConfig)

  val genFrameStyle: Gen[PrintFrameStyle] = for {
    value <- genConfigItemValue
    displayName <- genConfigItemDisplayName
    selected <- Arbitrary.arbBool.arbitrary
  } yield PrintFrameStyle(value, displayName, selected)

  val genFramedPrintConfig: Gen[FramedPrintConfig] = for {
    size <- genProductSize
    style <- genFrameStyle
    frameColour <- genFrameColour
    matteColour <- genMatteColour
  } yield FramedPrintConfig(Seq(size), Seq(style), Seq(frameColour), Seq(matteColour), Nil)

  implicit val arbFramedPrintConfig = Arbitrary(genFramedPrintConfig)

  val genPhoneCaseConfig: Gen[PhoneCaseConfig] = for {
    model <- genPhoneModel
    cover <- genPhoneCover
  } yield PhoneCaseConfig(Seq(model), Seq(cover), Nil)

  implicit val arbPhoneCaseConfig = Arbitrary(genPhoneCaseConfig)

  val genProductConfig: Gen[ProductConfiguration] = Gen.oneOf(genApparelConfig, genFramedPrintConfig, genPhoneCaseConfig)

  implicit val arbProductConfiguration: Arbitrary[ProductConfiguration] = Arbitrary(genProductConfig)

  val genWorkId: Gen[WorkId] = genPositiveInt.map(i => WorkId(i.toString))

  implicit val arbWorkId = Arbitrary(genWorkId)

  val genWorkTitle: Gen[WorkTitle] = genNonEmptyString.map(WorkTitle)

  implicit val arbWorkTitle = Arbitrary(genWorkTitle)

  val genWork: Gen[Work] = for {
    workId <- genWorkId
    title <- genWorkTitle
    artist <- genArtist
    webLink <- genUrl
    remoteKey <- genPositiveInt.map(WorkImageRemoteKey)
    size <- genLargeImageSizes
  } yield {
    // WorkImageDefinition must have the same work id as the work
    val imageDefinition = WorkImageDefinition(remoteKey, workId, size)
    Work(workId, title, artist, imageDefinition, webLink)
  }

  implicit val arbWork = Arbitrary(genWork)

  val genImageSize: Gen[IrregularImageSize] = for {
    width <- Gen.chooseNum(0, MaxImageDimension).map(PixelWidth)
    height <- Gen.chooseNum(0, MaxImageDimension).map(PixelHeight)
  } yield IrregularImageSize(width, height)

  implicit val arbImageSize = Arbitrary(genImageSize)

  val genImage: Gen[Image] = genImageUrl.map(Image.imageFromUrl)

  val genImageNoSize: Gen[Image] = genNoSizeImageUrl.map(Image.imageFromUrl)

  implicit val arbImage = Arbitrary(genImage)

  val genProductId: Gen[ProductId] = genNonEmptyString.map(ProductId)

  val genProductInfo: Gen[ProductInfo] = for {
    s <- genNonEmptyString.map(s => Seq(s))
  } yield ProductInfo(s)

  val genProduct: Gen[Product] = for {
    productId <- genProductId
    workId <- genWorkId
    info <- genProductInfo
    price <- genPrice
    webLink <- genUrl
    definition <- genProductDefinition
    image <- genImageNoSize
    options <- genProductConfig
  } yield Product(productId, workId, definition, info, Some(options), price, webLink, Seq(image))

  implicit val arbProduct = Arbitrary(genProduct)

  val genSupportedProductDefinition: Gen[ProductDefinition] = for {
    definition <- genProductDefinition
    newTypeId <- Gen.oneOf(SupportedProduct.supportedProducts).map(_.typeId)
  } yield definition.copy(typeId = newTypeId)

  val genSupportedProductProduct: Gen[Product] = for {
    product <- genProduct
    newDefinition <- genSupportedProductDefinition
  } yield product.copy(definition = newDefinition)

  val genStandardProduct: Gen[Product] = for {
    product <- genProduct
    config <- genApparelConfig
  } yield product.copy(config = Some(config))

  val genApparelProduct: Gen[Product] = for {
    product <- genProduct
    config <- genApparelConfig
  } yield product.copy(
    config = Some(config),
    definition = ProductDefinition(UnisexTee.typeId, ProductDisplayName("Unisex Tee"))
  )

  val genFramedPrintProduct: Gen[Product] = for {
    product <- genProduct
    config <- genFramedPrintConfig
  } yield product.copy(
    config = Some(config),
    definition = ProductDefinition(FramedPrint.typeId, ProductDisplayName("Framed Print"))
  )

  val genPhoneCaseProduct: Gen[Product] = for {
    product <- genProduct
    config <- genPhoneCaseConfig
  } yield product.copy(
    config = Some(config),
    definition = ProductDefinition(GalaxyCase.typeId, ProductDisplayName("Samsung Galaxy Case/Skin"))
  )

  val genSearchResultMetadata: Gen[ResultMetadata] = for {
    keywords <- genNonEmptyString.map(SearchKeywords)
    totalProducts <- genPositiveInt.map(TotalProducts)
    totalPages <- genPositiveInt.map(TotalPages)
    currentPage <- Gen.chooseNum(0, totalPages).map(Page)
    resultsPerPage <- Gen.chooseNum(0, 96).map(ResultsPerPage)
  } yield ResultMetadata(keywords, totalProducts, currentPage, totalPages, resultsPerPage)

  val genRawSearchResult: Gen[RawSearchResult] = for {
    product <- genProduct
    metadata <- genSearchResultMetadata
  } yield {
    val p = product.copy(config = None)
    RawSearchResult(Seq(p), metadata)
  }

  val genNoSelectedConfig: Gen[Seq[(ConfigItemName, ConfigItemLabel)]] = Gen.const(Seq.empty[(ConfigItemName, ConfigItemLabel)])

  val genSelectedConfig: Gen[Seq[(ConfigItemName, ConfigItemLabel)]] = for {
    tuple <- genApparelConfig.map(c => configItemAsFlatConfig(c.nonDisplayableConfig).map(f => (f.name, f.label)))
  } yield tuple

  val genSelectedProductConfig: Gen[SelectedProductConfig] = for {
    typeId <- genProductTypeId
    config <- Gen.oneOf(genSelectedConfig, genNoSelectedConfig)
  } yield SelectedProductConfig(typeId, config)

  val genCartProduct: Gen[CartProduct] = for {
    workId <- genWorkId
    config <- genSelectedProductConfig
    quantity <- genPositiveInt.map(Quantity)
  } yield CartProduct(workId, config, quantity)
}

object ModelGenerators extends ModelGenerators
