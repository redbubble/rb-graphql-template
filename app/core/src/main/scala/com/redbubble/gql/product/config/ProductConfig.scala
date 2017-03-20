package com.redbubble.gql.product.config

import com.redbubble.gql.product.PhoneCoverType
import com.redbubble.gql.product.config.ProductConfiguration.configItemAsFlatConfig

trait ProductConfigurationItem {
  def label: ConfigItemLabel

  def name: ConfigItemName

  def value: ConfigItemValue

  def valueDisplayName: ConfigItemDisplayName

  def defaultSelection: Boolean
}

final case class FlattenedConfigItem(name: ConfigItemName, label: ConfigItemLabel, value: ConfigItemValue,
    valueDisplayName: ConfigItemDisplayName, defaultSelection: Boolean) extends ProductConfigurationItem

/**
  * Configuration, or options for a particular product.
  *
  * We care about, and treat sizes and colours specially (visually) in the iOS app, so we reify them here. All other
  * options from the API are bundled uncerimoniously into `extraConfig`.
  */
sealed trait ProductConfiguration {
  /**
    * The colours this product is available in, if any.
    */
  def availableColours: Option[Seq[ProductColour]]

  /**
    * The sizes this product is available in, if any.
    */
  def availableSizes: Option[Seq[ProductSize]]

  /**
    * Additional configuration options for a product, that are not sizes and colours. Useful for non-apparel products.
    * These will be displayed in the app.
    */
  def additionalConfig: Seq[FlattenedConfigItem]

  /**
    * Config that isn't for display, but may be required for sending back to the API in subsequent calls. For
    * example non-customisable t-shirt options, which are required when computing a price.
    */
  def nonDisplayableConfig: Seq[FlattenedConfigItem]
}

final case class ApparelConfig(sizes: Seq[ProductSize],
    colours: Seq[ApparelColour], nonDisplayableConfig: Seq[FlattenedConfigItem]) extends ProductConfiguration {
  override val availableColours = Some(colours)

  override val availableSizes = Some(sizes)

  override lazy val additionalConfig = Nil
}

final case class MiniSkirtConfig(
    sizes: Seq[ProductSize], nonDisplayableConfig: Seq[FlattenedConfigItem]) extends ProductConfiguration {

  override val availableColours = None

  override val availableSizes = Some(sizes)

  override lazy val additionalConfig = Nil
}

final case class LeggingConfig(
    sizes: Seq[ProductSize], nonDisplayableConfig: Seq[FlattenedConfigItem]) extends ProductConfiguration {

  override val availableColours = None

  override val availableSizes = Some(sizes)

  override lazy val additionalConfig = Nil
}

final case class WallClockConfig(frameColours: Seq[FrameColour], handColours: Seq[HandColour],
    nonDisplayableConfig: Seq[FlattenedConfigItem]) extends ProductConfiguration {

  override val availableColours = None

  override val availableSizes = None

  override lazy val additionalConfig =
    Seq(frameColours, handColours).flatMap(configItemAsFlatConfig)
}

final case class FramedPrintConfig(sizes: Seq[ProductSize], frameStyles: Seq[PrintFrameStyle],
    frameColours: Seq[FrameColour], matteColours: Seq[MatteColour],
    nonDisplayableConfig: Seq[FlattenedConfigItem]) extends ProductConfiguration {

  override val availableColours = None

  override val availableSizes = Some(sizes)

  override lazy val additionalConfig =
    Seq(frameStyles, frameColours, matteColours).flatMap(configItemAsFlatConfig)
}

final case class GalleryBoardConfig(
    sizes: Seq[ProductSize], nonDisplayableConfig: Seq[FlattenedConfigItem]) extends ProductConfiguration {
  override val availableColours = None

  override val availableSizes = Some(sizes)

  override lazy val additionalConfig = Nil
}

final case class PhoneCaseConfig(models: Seq[PhoneModel],
    coverTypes: Seq[PhoneCoverType], nonDisplayableConfig: Seq[FlattenedConfigItem]) extends ProductConfiguration {
  override val availableColours = None

  override val availableSizes = None

  override lazy val additionalConfig = Seq(models, coverTypes).flatMap(configItemAsFlatConfig)
}

final case class DeviceCaseConfig(
    styles: Seq[DeviceCaseStyle], nonDisplayableConfig: Seq[FlattenedConfigItem]) extends ProductConfiguration {
  override val availableColours = None

  override val availableSizes = None

  override lazy val additionalConfig = configItemAsFlatConfig(styles)
}

final case class LaptopSleeveConfig(
    sizes: Seq[ProductSize], nonDisplayableConfig: Seq[FlattenedConfigItem]) extends ProductConfiguration {
  override val availableColours = None

  override val availableSizes = Some(sizes)

  override lazy val additionalConfig = Nil
}

final case class JournalConfig(
    paperTypes: Seq[PaperType], nonDisplayableConfig: Seq[FlattenedConfigItem]) extends ProductConfiguration {
  override val availableColours = None

  override val availableSizes = None

  override lazy val additionalConfig = configItemAsFlatConfig(paperTypes)
}

final case class PrintConfig(sizes: Seq[ProductSize],
    finishes: Seq[PrintFinish], nonDisplayableConfig: Seq[FlattenedConfigItem]) extends ProductConfiguration {
  override val availableColours = None

  override val availableSizes = Some(sizes)

  override lazy val additionalConfig = configItemAsFlatConfig(finishes)
}

final case class GreetingCardConfig(
    cardSizes: Seq[GreetingCardSize], nonDisplayableConfig: Seq[FlattenedConfigItem]) extends ProductConfiguration {
  override val availableColours = None

  override val availableSizes = None

  override lazy val additionalConfig = configItemAsFlatConfig(cardSizes)
}

final case class ArtPrintConfig(
    sizes: Seq[ProductSize], nonDisplayableConfig: Seq[FlattenedConfigItem]) extends ProductConfiguration {
  override val availableColours = None

  override val availableSizes = Some(sizes)

  override lazy val additionalConfig = Nil
}

final case class MugConfig(
    styles: Seq[MugStyle], nonDisplayableConfig: Seq[FlattenedConfigItem]) extends ProductConfiguration {
  override val availableColours = None

  override val availableSizes = None

  override lazy val additionalConfig = configItemAsFlatConfig(styles)
}

final case class AcrylicBlockConfig(
    sizes: Seq[ProductSize], nonDisplayableConfig: Seq[FlattenedConfigItem]) extends ProductConfiguration {
  override val availableColours = None

  override val availableSizes = Some(sizes)

  override lazy val additionalConfig = Nil
}

final case class DuvetConfig(
    sizes: Seq[ProductSize], nonDisplayableConfig: Seq[FlattenedConfigItem]) extends ProductConfiguration {
  override val availableColours = None

  override val availableSizes = Some(sizes)

  override lazy val additionalConfig = Nil
}

final case class StickerConfig(
    sizes: Seq[ProductSize], nonDisplayableConfig: Seq[FlattenedConfigItem]) extends ProductConfiguration {
  override val availableColours = None

  override val availableSizes = Some(sizes)

  override lazy val additionalConfig = Nil
}

final case class TapestryConfig(
    sizes: Seq[ProductSize], nonDisplayableConfig: Seq[FlattenedConfigItem]) extends ProductConfiguration {
  override val availableColours = None

  override val availableSizes = Some(sizes)

  override lazy val additionalConfig = Nil
}

final case class ThrowPillowConfig(sizes: Seq[ProductSize],
    types: Seq[ThrowPillowType], nonDisplayableConfig: Seq[FlattenedConfigItem]) extends ProductConfiguration {
  override val availableColours = None

  override val availableSizes = Some(sizes)

  override lazy val additionalConfig = configItemAsFlatConfig(types)
}

final case class StudioPouchConfig(
    sizes: Seq[ProductSize], nonDisplayableConfig: Seq[FlattenedConfigItem]) extends ProductConfiguration {
  override val availableColours = None

  override val availableSizes = Some(sizes)

  override lazy val additionalConfig = Nil
}

final case class ToteBagConfig(
    sizes: Seq[ProductSize], nonDisplayableConfig: Seq[FlattenedConfigItem]) extends ProductConfiguration {
  override val availableColours = None

  override val availableSizes = Some(sizes)

  override lazy val additionalConfig = Nil
}

final case class ScarfConfig(
    sizes: Seq[ProductSize], nonDisplayableConfig: Seq[FlattenedConfigItem]) extends ProductConfiguration {
  override val availableColours = None

  override val availableSizes = Some(sizes)

  override lazy val additionalConfig = Nil
}

final case class NoConfig() extends ProductConfiguration {
  override val availableColours = None

  override val availableSizes = None

  override val additionalConfig = Nil

  override val nonDisplayableConfig = Nil
}

object ProductConfiguration {
  def noConfig: ProductConfiguration = NoConfig()

  def configItemAsFlatConfig[C <: ProductConfigurationItem](cs: Seq[C]): Seq[FlattenedConfigItem] =
    cs.map { c =>
      FlattenedConfigItem(
        ConfigItemName(c.name), ConfigItemLabel(c.label),
        ConfigItemValue(c.value), ConfigItemDisplayName(c.valueDisplayName), c.defaultSelection
      )
    }
}
