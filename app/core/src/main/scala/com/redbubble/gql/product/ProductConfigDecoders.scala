package com.redbubble.gql.product

import com.redbubble.gql.product.SupportedProduct.supportedProducts
import com.redbubble.gql.product.config.{ApparelConfig, ProductConfiguration, _}
import com.redbubble.util.json.DecoderOps
import io.circe.Decoder
import io.circe.Decoder._

trait ProductConfigDecoders extends ExtraConfigDecoders with DecoderOps {
  private val productSizesDecoder: Decoder[ProductSize] = Decoder.instance { c =>
    for {
      value <- c.downField("value").as[String].map(ConfigItemValue)
      name <- c.downField("display_name").as[String].map(ConfigItemDisplayName)
      selected <- c.downField("selected").as[Boolean]
    } yield ProductSize(value, name, None, selected)
  }

  private val productSizesSeqDecoder = decodeFocusSequence(productSizesDecoder)

  private val apparelSizeMeasurementsDecoder: Decoder[ApparelSizeMeasurement] = Decoder.instance { c =>
    for {
      label <- c.downField("label").as[String].map(MeasurementLabel)
      chest <- c.downField("chest").as[String].map(MeasurementChest)
      length <- c.downField("length").as[String].map(MeasurementLength)
    } yield ApparelSizeMeasurement(label, chest, length)
  }

  private val apparelSizeMeasurementsSeqDecoder = decodeFocusSequence(apparelSizeMeasurementsDecoder)

  // We associate the "label" of a measurement with the "display_name" of a size. This however, could mean we leave
  // orphan measurements.
  private def apparelSizeDecoder(measurements: Seq[ApparelSizeMeasurement]): Decoder[ProductSize] = Decoder.instance { c =>
    for {
      value <- c.downField("value").as[String].map(ConfigItemValue)
      name <- c.downField("display_name").as[String].map(ConfigItemDisplayName)
      selected <- c.downField("selected").as[Boolean]
    } yield ProductSize(value, name, measurements.find(m => m.label == name), selected)
  }

  private def apparelSizeSeqDecoder(measurements: Seq[ApparelSizeMeasurement]) =
    decodeFocusSequence(apparelSizeDecoder(measurements))

  private val apparelColourDecoder: Decoder[ApparelColour] = Decoder.instance { c =>
    for {
      value <- c.downField("value").as[String].map(ConfigItemValue)
      name <- c.downField("display_name").as[String].map(ConfigItemDisplayName)
      hex <- c.downField("hex").as[String].map(ColourHex)
      selected <- c.downField("selected").as[Boolean]
    } yield ApparelColour(value, name, hex, selected)
  }

  private val apparelColourSeqDecoder = decodeFocusSequence(apparelColourDecoder)

  private val frameColourDecoder: Decoder[FrameColour] = Decoder.instance { c =>
    for {
      value <- c.downField("value").as[String].map(ConfigItemValue)
      name <- c.downField("display_name").as[String].map(ConfigItemDisplayName)
      selected <- c.downField("selected").as[Boolean]
    } yield FrameColour(value, name, selected)
  }

  private val frameColourSeqDecoder = decodeFocusSequence(frameColourDecoder)

  private val matteColourDecoder: Decoder[MatteColour] = Decoder.instance { c =>
    for {
      value <- c.downField("value").as[String].map(ConfigItemValue)
      name <- c.downField("display_name").as[String].map(ConfigItemDisplayName)
      selected <- c.downField("selected").as[Boolean]
    } yield MatteColour(value, name, selected)
  }

  private val matteColourSeqDecoder = decodeFocusSequence(matteColourDecoder)

  private val handColourDecoder: Decoder[HandColour] = Decoder.instance { c =>
    for {
      value <- c.downField("value").as[String].map(ConfigItemValue)
      name <- c.downField("display_name").as[String].map(ConfigItemDisplayName)
      selected <- c.downField("selected").as[Boolean]
    } yield HandColour(value, name, selected)
  }

  private val handColourSeqDecoder = decodeFocusSequence(handColourDecoder)

  private val frameStyleDecoder: Decoder[PrintFrameStyle] = Decoder.instance { c =>
    for {
      value <- c.downField("value").as[String].map(ConfigItemValue)
      name <- c.downField("display_name").as[String].map(ConfigItemDisplayName)
      selected <- c.downField("selected").as[Boolean]
    } yield PrintFrameStyle(value, name, selected)
  }

  private val frameStyleSeqDecoder = decodeFocusSequence(frameStyleDecoder)

  val wallClockConfigDecoder: Decoder[ProductConfiguration] = Decoder.instance { c =>
    val nonDisplay = parseExtraConfigFields(c).filterNot(parsedFields(Seq("frame_color", "hand_color")))
    for {
      frameColours <- c.downField("frame_colors").downField("options").as[Seq[FrameColour]](frameColourSeqDecoder)
      handColours <- c.downField("hand_colors").downField("options").as[Seq[HandColour]](handColourSeqDecoder)
    } yield WallClockConfig(frameColours, handColours, nonDisplay)
  }

  val framedPrintConfigDecoder: Decoder[ProductConfiguration] = Decoder.instance { c =>
    val nonDisplay = parseExtraConfigFields(c).filterNot(parsedFields(Seq("size", "frame_style", "frame_color", "matte_color")))
    for {
      sizes <- c.downField("sizes").downField("options").as[Seq[ProductSize]](productSizesSeqDecoder)
      frameStyles <- c.downField("frame_styles").downField("options").as[Seq[PrintFrameStyle]](frameStyleSeqDecoder)
      frameColours <- c.downField("frame_colors").downField("options").as[Seq[FrameColour]](frameColourSeqDecoder)
      matteColours <- c.downField("matte_colors").downField("options").as[Seq[MatteColour]](matteColourSeqDecoder)
    } yield FramedPrintConfig(sizes, frameStyles, frameColours, matteColours, nonDisplay)
  }

  val galleryBoardConfigDecoder: Decoder[ProductConfiguration] = Decoder.instance { c =>
    val nonDisplay = parseExtraConfigFields(c).filterNot(parsedFields(Seq("size")))
    for {
      sizes <- c.downField("sizes").downField("options").as[Seq[ProductSize]](productSizesSeqDecoder)
    } yield GalleryBoardConfig(sizes, nonDisplay)
  }

  private val printFinishesDecoder: Decoder[PrintFinish] = Decoder.instance { c =>
    for {
      value <- c.downField("value").as[String].map(ConfigItemValue)
      name <- c.downField("display_name").as[String].map(ConfigItemDisplayName)
      selected <- c.downField("selected").as[Boolean]
    } yield PrintFinish(value, name, selected)
  }

  private val printFinishesSeqDecoder = decodeFocusSequence(printFinishesDecoder)

  val printConfigDecoder: Decoder[ProductConfiguration] = Decoder.instance { c =>
    val nonDisplay = parseExtraConfigFields(c).filterNot(parsedFields(Seq("size", "finish")))
    for {
      sizes <- c.downField("sizes").downField("options").as[Seq[ProductSize]](productSizesSeqDecoder)
      finishes <- c.downField("finishes").downField("options").as[Seq[PrintFinish]](printFinishesSeqDecoder)
    } yield PrintConfig(sizes, finishes, nonDisplay)
  }

  private val paperTypeDecoder: Decoder[PaperType] = Decoder.instance { c =>
    for {
      value <- c.downField("value").as[String].map(ConfigItemValue)
      name <- c.downField("display_name").as[String].map(ConfigItemDisplayName)
      selected <- c.downField("selected").as[Boolean]
    } yield PaperType(value, name, selected)
  }

  private val paperTypeSeqDecoder = decodeFocusSequence(paperTypeDecoder)

  val journalConfigDecoder: Decoder[ProductConfiguration] = Decoder.instance { c =>
    val nonDisplay = parseExtraConfigFields(c).filterNot(parsedFields(Seq("paper_type")))
    for {
      paperTypes <- c.downField("paper_types").downField("options").as[Seq[PaperType]](paperTypeSeqDecoder)
    } yield JournalConfig(paperTypes, nonDisplay)
  }

  private val cardSizeDecoder: Decoder[GreetingCardSize] = Decoder.instance { c =>
    for {
      value <- c.downField("value").as[String].map(ConfigItemValue)
      name <- c.downField("display_name").as[String].map(ConfigItemDisplayName)
      selected <- c.downField("selected").as[Boolean]
    } yield GreetingCardSize(value, name, selected)
  }

  private val cardSizeSeqDecoder = decodeFocusSequence(cardSizeDecoder)

  private val phoneModelsDecoder: Decoder[PhoneModel] = Decoder.instance { c =>
    for {
      value <- c.downField("value").as[String].map(ConfigItemValue)
      name <- c.downField("display_name").as[String].map(ConfigItemDisplayName)
      selected <- c.downField("selected").as[Boolean]
    } yield PhoneModel(value, name, selected)
  }

  private val phoneModelsSeqDecoder = decodeFocusSequence(phoneModelsDecoder)

  private val phoneCoversDecoder: Decoder[PhoneCoverType] = Decoder.instance { c =>
    for {
      value <- c.downField("value").as[String].map(ConfigItemValue)
      name <- c.downField("display_name").as[String].map(ConfigItemDisplayName)
      selected <- c.downField("selected").as[Boolean]
    } yield PhoneCoverType(value, name, selected)
  }

  private val phoneCoversSeqDecoder = decodeFocusSequence(phoneCoversDecoder)

  val phoneCaseConfigDecoder: Decoder[ProductConfiguration] = Decoder.instance { c =>
    val nonDisplay = parseExtraConfigFields(c).filterNot(parsedFields(Seq("phone_model", "cover_type")))
    for {
      sizes <- c.downField("models").downField("options").as[Seq[PhoneModel]](phoneModelsSeqDecoder)
      covers <- c.downField("cover_types").downField("options").as[Seq[PhoneCoverType]](phoneCoversSeqDecoder)
    } yield PhoneCaseConfig(sizes, covers, nonDisplay)
  }

  private val throwPillowTypeDecoder: Decoder[ThrowPillowType] = Decoder.instance { c =>
    for {
      value <- c.downField("value").as[String].map(ConfigItemValue)
      name <- c.downField("display_name").as[String].map(ConfigItemDisplayName)
      selected <- c.downField("selected").as[Boolean]
    } yield ThrowPillowType(value, name, selected)
  }

  private val throwPillowTypeSeqDecoder = decodeFocusSequence(throwPillowTypeDecoder)

  /**
    * A basic decoder for a product's configuration that returns no config (i.e. does not acually parse any config).
    */
  val basicDecoder: Decoder[ProductConfiguration] = Decoder.const(ProductConfiguration.noConfig)

  val apparelConfigDecoder: Decoder[ProductConfiguration] = Decoder.instance { c =>
    val nonDisplay = parseExtraConfigFields(c).filterNot(parsedFields(Seq("size", "body_color")))
    for {
      details <- c.up.downField("sizing_details").downField("measurements").as[Seq[ApparelSizeMeasurement]](apparelSizeMeasurementsSeqDecoder)
      sizes <- c.downField("sizes").downField("options").as[Seq[ProductSize]](apparelSizeSeqDecoder(details))
      colours <- c.downField("body_color").as[Seq[ApparelColour]](apparelColourSeqDecoder)
    } yield ApparelConfig(sizes, colours, nonDisplay)
  }

  val miniSkirtConfigDecoder: Decoder[ProductConfiguration] = Decoder.instance { c =>
    val nonDisplay = parseExtraConfigFields(c).filterNot(parsedFields(Seq("size")))
    for {
      sizes <- c.downField("sizes").downField("options").as[Seq[ProductSize]](apparelSizeSeqDecoder(Seq.empty))
    } yield MiniSkirtConfig(sizes, nonDisplay)
  }

  val leggingsConfigDecoder: Decoder[ProductConfiguration] = Decoder.instance { c =>
    val nonDisplay = parseExtraConfigFields(c).filterNot(parsedFields(Seq("size")))
    for {
      sizes <- c.downField("sizes").downField("options").as[Seq[ProductSize]](apparelSizeSeqDecoder(Seq.empty))
    } yield LeggingConfig(sizes, nonDisplay)
  }

  val greetingCardConfigDecoder: Decoder[ProductConfiguration] = Decoder.instance { c =>
    val nonDisplay = parseExtraConfigFields(c).filterNot(parsedFields(Seq("card_size")))
    for {
      sizes <- c.downField("sizes").downField("options").as[Seq[GreetingCardSize]](cardSizeSeqDecoder)
    } yield GreetingCardConfig(sizes, nonDisplay)
  }

  val artPrintConfigDecoder: Decoder[ProductConfiguration] = Decoder.instance { c =>
    val nonDisplay = parseExtraConfigFields(c).filterNot(parsedFields(Seq("size")))
    for {
      sizes <- c.downField("sizes").downField("options").as[Seq[ProductSize]](productSizesSeqDecoder)
    } yield ArtPrintConfig(sizes, nonDisplay)
  }

  val duvetConfigDecoder: Decoder[ProductConfiguration] = Decoder.instance { c =>
    val nonDisplay = parseExtraConfigFields(c).filterNot(parsedFields(Seq("size")))
    for {
      sizes <- c.downField("sizes").downField("options").as[Seq[ProductSize]](productSizesSeqDecoder)
    } yield DuvetConfig(sizes, nonDisplay)
  }

  val stickerDecoder: Decoder[ProductConfiguration] = Decoder.instance { c =>
    val nonDisplay = parseExtraConfigFields(c).filterNot(parsedFields(Seq("size")))
    for {
      sizes <- c.downField("sizes").downField("options").as[Seq[ProductSize]](productSizesSeqDecoder)
    } yield StickerConfig(sizes, nonDisplay)
  }

  val tapestryDecoder: Decoder[ProductConfiguration] = Decoder.instance { c =>
    val nonDisplay = parseExtraConfigFields(c).filterNot(parsedFields(Seq("size")))
    for {
      sizes <- c.downField("sizes").downField("options").as[Seq[ProductSize]](productSizesSeqDecoder)
    } yield TapestryConfig(sizes, nonDisplay)
  }

  val throwPillowDecoder: Decoder[ProductConfiguration] = Decoder.instance { c =>
    val nonDisplay = parseExtraConfigFields(c).filterNot(parsedFields(Seq("size", "type")))
    for {
      sizes <- c.downField("sizes").downField("options").as[Seq[ProductSize]](productSizesSeqDecoder)
      types <- c.downField("types").downField("options").as[Seq[ThrowPillowType]](throwPillowTypeSeqDecoder)
    } yield ThrowPillowConfig(sizes, types, nonDisplay)
  }

  val studioPouchDecoder: Decoder[ProductConfiguration] = Decoder.instance { c =>
    val nonDisplay = parseExtraConfigFields(c).filterNot(parsedFields(Seq("size")))
    for {
      sizes <- c.downField("sizes").downField("options").as[Seq[ProductSize]](productSizesSeqDecoder)
    } yield StudioPouchConfig(sizes, nonDisplay)
  }

  val toteBagDecoder: Decoder[ProductConfiguration] = Decoder.instance { c =>
    val nonDisplay = parseExtraConfigFields(c).filterNot(parsedFields(Seq("size")))
    for {
      sizes <- c.downField("sizes").downField("options").as[Seq[ProductSize]](productSizesSeqDecoder)
    } yield ToteBagConfig(sizes, nonDisplay)
  }

  val scarfDecoder: Decoder[ProductConfiguration] = Decoder.instance { c =>
    val nonDisplay = parseExtraConfigFields(c).filterNot(parsedFields(Seq("size")))
    for {
      sizes <- c.downField("sizes").downField("options").as[Seq[ProductSize]](productSizesSeqDecoder)
    } yield ScarfConfig(sizes, nonDisplay)
  }

  private val mugStyleDecoder: Decoder[MugStyle] = Decoder.instance { c =>
    for {
      value <- c.downField("value").as[String].map(ConfigItemValue)
      name <- c.downField("display_name").as[String].map(ConfigItemDisplayName)
      selected <- c.downField("selected").as[Boolean]
    } yield MugStyle(value, name, selected)
  }

  private val mugStyleSeqDecoder = decodeFocusSequence(mugStyleDecoder)

  val mugDecoder: Decoder[ProductConfiguration] = Decoder.instance { c =>
    val nonDisplay = parseExtraConfigFields(c).filterNot(parsedFields(Seq("style")))
    for {
      styles <- c.downField("styles").downField("options").as[Seq[MugStyle]](mugStyleSeqDecoder)
    } yield MugConfig(styles, nonDisplay)
  }

  val acrylicBlockDecoder: Decoder[ProductConfiguration] = Decoder.instance { c =>
    val nonDisplay = parseExtraConfigFields(c).filterNot(parsedFields(Seq("size")))
    for {
      sizes <- c.downField("sizes").downField("options").as[Seq[ProductSize]](productSizesSeqDecoder)
    } yield AcrylicBlockConfig(sizes, nonDisplay)
  }

  private val deviceCaseStyleDecoder: Decoder[DeviceCaseStyle] = Decoder.instance { c =>
    for {
      value <- c.downField("value").as[String].map(ConfigItemValue)
      name <- c.downField("display_name").as[String].map(ConfigItemDisplayName)
      selected <- c.downField("selected").as[Boolean]
    } yield DeviceCaseStyle(value, name, selected)
  }

  private val deviceCaseStyleSeqDecoder = decodeFocusSequence(deviceCaseStyleDecoder)

  val deviceCaseDecoder: Decoder[ProductConfiguration] = Decoder.instance { c =>
    val nonDisplay = parseExtraConfigFields(c).filterNot(parsedFields(Seq("style")))
    for {
      styles <- c.downField("styles").downField("options").as[Seq[DeviceCaseStyle]](deviceCaseStyleSeqDecoder)
    } yield DeviceCaseConfig(styles, nonDisplay)
  }

  val sleeveDecoder: Decoder[ProductConfiguration] = Decoder.instance { c =>
    val nonDisplay = parseExtraConfigFields(c).filterNot(parsedFields(Seq("size")))
    for {
      sizes <- c.downField("sizes").downField("options").as[Seq[ProductSize]](productSizesSeqDecoder)
    } yield LaptopSleeveConfig(sizes, nonDisplay)
  }

  /**
    * Returns a product-specific decoder for that product's configuration.
    */
  def productConfigurationDecoder(product: ProductDefinition): Decoder[ProductConfiguration] = {
    supportedProducts.find(sp => sp.typeId == product.typeId).map(_.configDecoder).getOrElse(basicDecoder)
  }

  // A predicate, that let's us remove fields that we've already got specific parsing handling for.
  private def parsedFields(ignoredOptionNames: Seq[String]): (FlattenedConfigItem) => Boolean =
    item => ignoredOptionNames.contains(item.name)
}

object ProductConfigDecoders extends ProductConfigDecoders
