package com.redbubble.gql.graphql.schema.types

import com.redbubble.graphql.ScalarTypes.stringScalarType
import com.redbubble.gql.graphql.schema.types.BaseTypes._
import com.redbubble.gql.graphql.schema.types.ImageTypes._
import com.redbubble.gql.graphql.schema.types.LocaleTypes._
import com.redbubble.gql.product._
import com.redbubble.gql.product.config._
import com.redbubble.gql.services.delivery.{DeliveryDate, ProductName}
import com.redbubble.gql.services.locale.CurrencyUnit
import com.redbubble.gql.services.locale.Locale.DefaultCurrency
import sangria.marshalling.ToInput.ScalarToInput
import sangria.schema.{Field, _}
import sangria.validation.ValueCoercionViolation

object ProductTypes {
  //
  // Product Code
  //

  private implicit val productNameInput = new ScalarToInput[ProductName]

  private case object ProductNameCoercionViolation extends ValueCoercionViolation(s"Product name expected")

  private def parseProductName(n: String): Either[ProductNameCoercionViolation.type, ProductName] = Right(ProductName(n))

  val ProductNameType: ScalarType[ProductName] = stringScalarType(
    "ProductName",
    "The name of an available product.",
    parseProductName, () => ProductNameCoercionViolation
  )

  val ProductNameArg = Argument("productName", ProductNameType, "The name of a product.")

  val PriceType = ObjectType(
    "Price", "An amount, a currency, a prefix & a unit.",
    fields[Unit, Price](
      Field(
        name = "amount",
        fieldType = BigDecimalType,
        description = Some("The price amount."),
        resolve = _.value.amount
      ),
      Field(
        name = "currency",
        fieldType = CurrencyCodeType,
        description = Some(s"The ISO code for the currency the price is in, e.g. ${DefaultCurrency.isoCode}."),
        resolve = _.value.currency.isoCode
      ),
      Field(
        name = "prefix",
        fieldType = StringType,
        description = Some(s"The prefix of the currency the price is in, e.g. ${DefaultCurrency.prefix.getOrElse("US")}."),
        resolve = _.value.currency.prefix.getOrElse(""),
        deprecationReason = Some("Prefer the local device's currency prefix instead of this, as it may not be defined/accurate.")
      ),
      Field(
        name = "unit",
        fieldType = StringType,
        description = Some(s"The currency unit the price is in, e.g. ${DefaultCurrency.unit.getOrElse("$")}."),
        resolve = _.value.currency.unit.orElse(DefaultCurrency.unit).getOrElse(CurrencyUnit("$")).toString,
        deprecationReason = Some("Prefer the local device's currency unit instead of this, as it may not be defined/accurate.")
      )
    )
  )

  val ProductConfigurationItemType = InterfaceType(
    name = "ProductConfigurationItem",
    description = "The configurable options for a product.",
    fields = fields[Unit, ProductConfigurationItem](
      Field("name", StringType, Some("The name (identifier/type) of the option, e.g. frame_style"), resolve = _.value.name),
      Field("label", StringType, Some("The label of the option, e.g. Frame Style"), resolve = _.value.label),
      Field("value", StringType, Some("The value of the option, e.g. black"), resolve = _.value.value),
      Field("valueDisplayName", StringType, Some("The display name for the value of the option, e.g. Black"), resolve = _.value.valueDisplayName),
      Field("defaultSelection", BooleanType,
        Some("Whether this item should be the default selection (when displayed with other configuration items of the same type)."),
        resolve = _.value.defaultSelection
      )
    )
  )

  val ApparelSizeMeasurementType = ObjectType(
    name = "ApparelSizeMeasurement",
    description = "A sizing measurement for an apparel product.",
    fields = fields[Unit, ApparelSizeMeasurement](
      Field("label", StringType,
        Some("The label for this measurement, e.g. XL. Maps to a display name for an apparel size."),
        resolve = _.value.label
      ),
      Field("chest", StringType, Some("The chest measurement for this size."), resolve = _.value.chest),
      Field("length", StringType, Some("The length measurement for this size."), resolve = _.value.length)
    )
  )

  private val sizeMeasurementTypes = List(ApparelSizeMeasurementType)

  val ProductSizeMeasurementType: UnionType[Unit] = UnionType[Unit](
    name = "ProductSizeMeasurement",
    description = Some(s"Detailed sizing measurements for a product size; one of: ${unionTypeNames(sizeMeasurementTypes)}."),
    types = sizeMeasurementTypes
  )

  val ProductSizeType = ObjectType(
    name = "ProductSize",
    description = "The sizes a product is available in.",
    interfaces = interfaces[Unit, ProductSize](ProductConfigurationItemType),
    fields = fields[Unit, ProductSize](
      Field(
        "measurement", OptionType(ProductSizeMeasurementType),
        Some(s"Detailed sizing measurements for a product size; one of: ${unionTypeNames(sizeMeasurementTypes)}."),
        resolve = _.value.measurement
      )
    )
  )

  val GreetingCardSizeType = ObjectType(
    name = "GreetingCardSize",
    description = "The sizes a greeting card is available in.",
    interfaces = interfaces[Unit, GreetingCardSize](ProductConfigurationItemType),
    fields = Nil
  )

  val MugStyleType = ObjectType(
    name = "MugStyle",
    description = "The styles a mug is available in.",
    interfaces = interfaces[Unit, MugStyle](ProductConfigurationItemType),
    fields = Nil
  )

  val ThrowPillowTypeType = ObjectType(
    name = "ThrowPillowType",
    description = "The types a throw pillow is available in.",
    interfaces = interfaces[Unit, ThrowPillowType](ProductConfigurationItemType),
    fields = Nil
  )

  val DeviceCaseStyleType = ObjectType(
    name = "DeviceCaseStyle",
    description = "The style (model) of device case.",
    interfaces = interfaces[Unit, DeviceCaseStyle](ProductConfigurationItemType),
    fields = Nil
  )

  val ProductColourType = InterfaceType(
    name = "ProductColour",
    description = "The colours an apparel product is available in, with an associated HEX colour value.",
    interfaces = interfaces[Unit, ProductColour](ProductConfigurationItemType),
    fields = fields[Unit, ProductColour](
      Field(
        "hexValue", OptionType(StringType),
        Some("The hexadecimal value of this colour, if it has one, e.g. 101010."),
        resolve = _.value.hexValue)
    )
  )

  val ApparelColourType = ObjectType(
    name = "ApparelColour",
    description = "A colour an apparel product is available in.",
    interfaces = interfaces[Unit, ApparelColour](ProductColourType),
    fields = fields[Unit, ApparelColour](
      Field("colour", StringType, Some("The hexadecimal value of this colour, e.g. 101010."), resolve = _.value.colour)
    )
  )

  val FrameColourType = ObjectType(
    name = "FrameColour",
    description = "The colour of a frame.",
    interfaces = interfaces[Unit, FrameColour](ProductColourType),
    fields = Nil
  )

  val HandColourType = ObjectType(
    name = "HandColour",
    description = "The colour of the hands on a clock.",
    interfaces = interfaces[Unit, HandColour](ProductColourType),
    fields = Nil
  )

  val MatteColourType = ObjectType(
    name = "MatteColour",
    description = "The colour of the background a print is framed with.",
    interfaces = interfaces[Unit, MatteColour](ProductColourType),
    fields = Nil
  )

  val PrintFrameStyleType = ObjectType(
    name = "PrintFrameStyle",
    description = "The types of frames available for a print.",
    interfaces = interfaces[Unit, PrintFrameStyle](ProductConfigurationItemType),
    fields = Nil
  )

  val PaperTypeType = ObjectType(
    name = "PaperType",
    description = "The types of paper this journal or notebook is available in.",
    interfaces = interfaces[Unit, PaperType](ProductConfigurationItemType),
    fields = Nil
  )

  val PrintFinishType = ObjectType(
    name = "PrintFinish",
    description = "The finishes this print is available in.",
    interfaces = interfaces[Unit, PrintFinish](ProductConfigurationItemType),
    fields = Nil
  )

  val PhoneModelType = ObjectType(
    name = "PhoneModel",
    description = "The models of phones this case is available for.",
    interfaces = interfaces[Unit, PhoneModel](ProductConfigurationItemType),
    fields = Nil
  )

  val PhoneCoverTypeType = ObjectType(
    name = "PhoneCoverType",
    description = "The types of covers available for this phone case.",
    interfaces = interfaces[Unit, PhoneCoverType](ProductConfigurationItemType),
    fields = Nil
  )

  val FlattenedConfigItemType = ObjectType(
    name = "FlattenedConfigItem",
    description = "Any additional configuration options for the product, that aren't handled directly.",
    interfaces = interfaces[Unit, FlattenedConfigItem](ProductConfigurationItemType),
    fields = Nil
  )

  private def sharedProductConfigFields[C <: ProductConfiguration]: List[Field[Unit, C]] =
    fields[Unit, C](
      Field(
        "availableColours", OptionType(ListType(ProductColourType)),
        Some("The colours this product is available in, if any."),
        resolve = _.value.availableColours
      ),
      Field(
        "availableSizes", OptionType(ListType(ProductSizeType)),
        Some("The sizes this product is available in, if any."),
        resolve = _.value.availableSizes
      ),
      Field(
        "addnConfig", ListType(FlattenedConfigItemType),
        Some("Additional configuration options for a product, that are not sizes and colours."),
        resolve = _.value.additionalConfig
      ),
      Field(
        "additionalConfig", ListType(FlattenedConfigItemType),
        Some("Additional configuration options for a product, that are not sizes and colours."),
        resolve = _.value.additionalConfig
      ),
      Field(
        "nonDisplayableConfig", ListType(FlattenedConfigItemType),
        Some("Config that isn't for display, but may be required for sending back to the API in subsequent calls."),
        resolve = _.value.nonDisplayableConfig
      )
    )

  private lazy val productConfigTypes = List(
    NoConfigType, ApparelConfigType, LeggingConfigType, FramedPrintConfigType, WallClockConfigType, PhoneCaseConfigType,
    JournalConfigType, PrintConfigType, ArtPrintConfigType, DeviceCaseConfigType, GreetingCardConfigType,
    GalleryBoardConfigType, LaptopSleeveConfigType, MugConfigType, AcrylicBlockConfigType, DuvetConfigType,
    StickerConfigType, TapestryConfigType, ThrowPillowConfigType, StudioPouchConfigType, ToteBagConfigType,
    ScarfConfigType
  )

  val GenericConfigType = ObjectType(
    name = "GenericConfig",
    description = s"Generic configuration for products; useful for sizes, colours, allConfig & extraConfig. For a typesafe form, use a fragment on one of union instances.",
    fields = sharedProductConfigFields[ProductConfiguration]
  )

  val NoConfigType = ObjectType(
    name = "NoConfig",
    description = "For products that have no confi  gurable options.",
    fields = sharedProductConfigFields[NoConfig]
  )

  val ApparelConfigType = ObjectType(
    name = "ApparelConfig",
    description = "Configuration for apparel products.",
    fields = sharedProductConfigFields[ApparelConfig] ++
        fields[Unit, ApparelConfig](
          Field(
            "sizes", ListType(ProductSizeType),
            Some("The sizes this product is available in."),
            resolve = _.value.sizes
          ),
          Field(
            "colours", ListType(ApparelColourType),
            Some("The colours this product is available in."),
            resolve = _.value.colours
          )
        )
  )

  val LeggingConfigType = ObjectType(
    name = "LeggingConfig",
    description = "Configuration for leggings.",
    fields = sharedProductConfigFields[LeggingConfig] ++
        fields[Unit, LeggingConfig](
          Field(
            "sizes", ListType(ProductSizeType),
            Some("The sizes this product is available in."),
            resolve = _.value.sizes
          )
        )
  )

  val FramedPrintConfigType = ObjectType(
    name = "FramedPrintConfig",
    description = "Configuration for framed prints.",
    fields = sharedProductConfigFields[FramedPrintConfig] ++
        fields[Unit, FramedPrintConfig](
          Field("sizes", ListType(ProductSizeType), Some("The available sizes of a product."), resolve = _.value.sizes),
          Field("frameStyles", ListType(PrintFrameStyleType), Some("The types of frames available for a print."), resolve = _.value.frameStyles),
          Field("frameColours", ListType(FrameColourType), Some("The colours of frame available."), resolve = _.value.frameColours),
          Field("matteColours", ListType(MatteColourType), Some("The backing board colours available."), resolve = _.value.matteColours)
        )
  )

  val WallClockConfigType = ObjectType(
    name = "WallClockConfig",
    description = "Configuration for wall clocks.",
    fields = sharedProductConfigFields[WallClockConfig] ++
        fields[Unit, WallClockConfig](
          Field("frameColours", ListType(FrameColourType), Some("The colours of frame available."), resolve = _.value.frameColours),
          Field("handColours", ListType(HandColourType), Some("The colours of hands available."), resolve = _.value.handColours)
        )
  )

  val PhoneCaseConfigType = ObjectType(
    name = "PhoneCaseConfig",
    description = "Configuration for phone cases.",
    fields = sharedProductConfigFields[PhoneCaseConfig] ++
        fields[Unit, PhoneCaseConfig](
          Field("models", ListType(PhoneModelType), Some("The models of phones this case is available for."), resolve = _.value.models),
          Field("coverTypes", ListType(PhoneCoverTypeType), Some("The cover types available for this phone."), resolve = _.value.coverTypes)
        )
  )

  val JournalConfigType = ObjectType(
    name = "JournalConfig",
    description = "Configuration for hardcover journals and spiral notebooks.",
    fields = sharedProductConfigFields[JournalConfig] ++
        fields[Unit, JournalConfig](
          Field(
            "paperTypes", ListType(PaperTypeType), Some("The types of paper this journal or notebook is available in."),
            resolve = _.value.paperTypes
          )
        )
  )

  val PrintConfigType = ObjectType(
    name = "PrintConfig",
    description = "Configuration for metal and photographic prints.",
    fields = sharedProductConfigFields[PrintConfig] ++
        fields[Unit, PrintConfig](
          Field("sizes", ListType(ProductSizeType), Some("The sizes this print is available in."), resolve = _.value.sizes),
          Field("finishes", ListType(PrintFinishType), Some("The finishes this print is available in."), resolve = _.value.finishes)
        )
  )

  val ArtPrintConfigType = ObjectType(
    name = "ArtPrintConfig",
    description = "Configuration for posters, art & canvas prints.",
    fields = sharedProductConfigFields[ArtPrintConfig] ++
        fields[Unit, ArtPrintConfig](
          Field("sizes", ListType(ProductSizeType), Some("The sizes this print is available in."), resolve = _.value.sizes)
        )
  )

  val GreetingCardConfigType = ObjectType(
    name = "GreetingCardConfig",
    description = "Greeting & post card configuration.",
    fields = sharedProductConfigFields[GreetingCardConfig] ++
        fields[Unit, GreetingCardConfig](
          Field("cardSizes", ListType(GreetingCardSizeType),
            Some("The sizes a greeting or post card is available in."),
            resolve = _.value.cardSizes
          )
        )
  )

  val GalleryBoardConfigType = ObjectType(
    name = "GalleryBoardConfig",
    description = "Gallery board configuration.",
    fields = sharedProductConfigFields[GalleryBoardConfig]
  )

  val LaptopSleeveConfigType = ObjectType(
    name = "LaptopSleeveConfig",
    description = "Laptop sleeve configuration.",
    fields = sharedProductConfigFields[LaptopSleeveConfig]
  )

  val MugConfigType = ObjectType(
    name = "MugConfig",
    description = "Mug configuration.",
    fields = sharedProductConfigFields[MugConfig] ++
        fields[Unit, MugConfig](
          Field("styles", ListType(MugStyleType), Some("The styles a mug is available in."), resolve = _.value.styles)
        )
  )

  val AcrylicBlockConfigType = ObjectType(
    name = "AcrylicBlockConfig",
    description = "Acrylic block configuration.",
    fields = sharedProductConfigFields[AcrylicBlockConfig]
  )

  val DuvetConfigType = ObjectType(
    name = "DuvetConfig",
    description = "Duvet cover configuration.",
    fields = sharedProductConfigFields[DuvetConfig]
  )

  val StickerConfigType = ObjectType(
    name = "StickerConfig",
    description = "Sticker configuration.",
    fields = sharedProductConfigFields[StickerConfig]
  )

  val TapestryConfigType = ObjectType(
    name = "TapestryConfig",
    description = "Wall tapestry configuration.",
    fields = sharedProductConfigFields[TapestryConfig]
  )

  val ThrowPillowConfigType = ObjectType(
    name = "ThrowPillowConfig",
    description = "Throw pillow configuration.",
    fields = sharedProductConfigFields[ThrowPillowConfig] ++
        fields[Unit, ThrowPillowConfig](
          Field("types", ListType(ThrowPillowTypeType),
            Some("The types a throw pillow is available in."),
            resolve = _.value.types
          )
        )
  )

  val StudioPouchConfigType = ObjectType(
    name = "StudioPouchConfig",
    description = "Studio pouch configuration.",
    fields = sharedProductConfigFields[StudioPouchConfig]
  )

  val ToteBagConfigType = ObjectType(
    name = "ToteBagConfig",
    description = "Tote bag configuration.",
    fields = sharedProductConfigFields[ToteBagConfig]
  )

  val ScarfConfigType = ObjectType(
    name = "ScarfConfig",
    description = "Scarf configuration.",
    fields = sharedProductConfigFields[ScarfConfig]
  )

  val DeviceCaseConfigType = ObjectType(
    name = "DeviceCaseConfig",
    description = "Configuration for device cases.",
    fields = sharedProductConfigFields[DeviceCaseConfig] ++
        fields[Unit, DeviceCaseConfig](
          Field("styles", ListType(DeviceCaseStyleType), Some("The styles (models) this case is available in."), resolve = _.value.styles)
        )
  )

  val ProductConfigurationType: UnionType[Unit] = UnionType[Unit](
    name = "ProductConfiguration",
    description =
        Some(s"Configurable options for a product; one of: ${unionTypeNames(productConfigTypes)}."),
    types = productConfigTypes
  )

  val SupportedProductType = ObjectType(
    "SupportedProduct", "Information about a product, which is supported by the iOS app.",
    fields[Unit, SupportedProduct](
      Field("typeId", StringType,
        Some("The identifier for the type of product this is."), resolve = _.value.typeId
      ),
      Field("searchTerm", StringType,
        Some("The term to use to constrain a search to just this product."), resolve = _.value.searchTerm
      )
    )
  )

  val ProductCategoryType = ObjectType(
    "ProductCategory", "The category a product is listed in (which could be in many).",
    fields[Unit, ProductCategory](
      Field("name", StringType, Some("The name of the category."), resolve = _.value.name),
      Field("products", ListType(SupportedProductType), Some("The products in this category."), resolve = _.value.products.toList)
    )
  )

  val ProductType = ObjectType(
    "Product", "A product, available for sale.",
    interfaces[Unit, Product](IdentifiableType),
    () => fields[Unit, Product](
      Field("workId", IDType, Some("The ID of the artwork this product is based on."), resolve = _.value.workId),
      Field("price", PriceType, Some("The price of the product."), resolve = _.value.price),
      Field("info", ListType(StringType), Some("The information about the product."), resolve = _.value.info),
      Field("images", ListType(ImageType), Some("The product's images."), resolve = _.value.images),
      Field("webLink", StringType, Some("The URL of the product on the Redbubble website."), resolve = _.value.webLink.toString),
      Field("typeId", StringType, Some("The identifier for the type of product this is."), resolve = _.value.definition.typeId),
      Field("typeName", StringType, Some("The display title/name of the product, e.g. Sticker."), resolve = _.value.definition.displayName),
      Field("categories", ListType(ProductCategoryType), Some("The categories this product is listed in."), resolve = _.value.categories),
      Field("config", OptionType(ProductConfigurationType),
        Some(s"Configurable options for a product; one of: ${unionTypeNames(ProductConfigurationType.types)}."),
        resolve = _.value.config),
      Field("genericConfig", OptionType(GenericConfigType),
        Some("Generic configuration for products; useful for sizes, colours, allConfig & extraConfig. For a specific, typesafe form, use `config`."),
        resolve = _.value.genericConfig)
    )
  )

  val FeaturedProductsType = ObjectType(
    "FeaturedProducts", "A short list of featured products for this work.",
    fields[Unit, FeaturedProducts](
      Field("workId", IDType, Some("The ID of the work."), resolve = _.value.workId),
      Field("featured", ListType(ProductType), Some("The featured products for the work."), resolve = _.value.featured),
      Field("totalProducts", IntType, Some("The total number of products for the work."), resolve = _.value.totalProducts)
    )
  )

  val DeliveryDateType = ObjectType(
    "DeliveryDate", "The estimated delivery dates for a product.",
    fields[Unit, DeliveryDate](
      Field("lowStandard", IntType, Some("The earliest estimated standard delivery date."), resolve = _.value.lowStandard),
      Field("highStandard", IntType, Some("The latest estimated stnadard delivery date."), resolve = _.value.highStandard),
      Field("lowExpress", IntType, Some("The earliest estimated express delivery date."), resolve = _.value.lowExpress),
      Field("highExpress", IntType, Some("The latest estimated express delivery date."), resolve = _.value.highExpress)
    )
  )
}
