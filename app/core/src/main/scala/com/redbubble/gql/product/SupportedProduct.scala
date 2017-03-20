package com.redbubble.gql.product

import com.redbubble.gql.core.{PixelHeight => height, PixelWidth => width, _}
import com.redbubble.gql.product.ProductConfigDecoders._
import com.redbubble.gql.product.SupportedProduct.{id, t}
import com.redbubble.gql.product.config.ProductConfiguration
import com.redbubble.gql.services.product._
import com.redbubble.util.enum.EnumerationMacros._
import io.circe.Decoder

/**
  * A product that is supported by the iOS API/app.
  *
  * To support a product in the API:
  * 1. Add an instance here, with it's IA code (as returned from the monolith APIs) & search term (ask DisCo).
  * 2. If required, write a new instance of `ProductConfiguration` for the product.
  * 3. Write a decoder for the JSON options that come back from the monolith, or use
  * [[ProductConfigDecoders.basicDecoder]], creating any associated types needed (e.g. special config items).
  * 4. Add it to the appropriate category in [[com.redbubble.gql.product.ProductCategory]].
  * 5. If it needs it, define a cropbox for the product's images (used when returned from a search).
  * 6. If the product requires special cart config, see [[SelectedProductConfig.cartConfig]] to add it.
  * 7. Expose the product in the GraphQL layer; see `GreetingCardConfigType` for example.
  *
  * Note. What we call `typeId` is the monolith's `ia_code`.
  */
sealed abstract class SupportedProduct(val typeId: ProductTypeId, val searchTerm: ProductSearchTerm) {
  /**
    * How to decode the configurable options for a product.
    */
  def configDecoder: Decoder[ProductConfiguration]

  /**
    * If required, a cropping box for the product images returned from the product APIs. This is used for search
    * results, where we want to zoom in on the artwork on the product, and not see the entire model.
    */
  def imageCropbox: Option[ImageCropBox]
}

// Apparel
case object ClassicTee extends SupportedProduct(id("u-tee-regular-crew"), t("classic-tees")) {
  override def configDecoder = apparelConfigDecoder

  override def imageCropbox = Some(ImageCropBox(Coordinate(width(138), height(299)), width(552), height(599)))
}

case object TriblendTee extends SupportedProduct(id("u-tee-triblend-slim-crew"), t("triblend-tees")) {
  override def configDecoder = apparelConfigDecoder

  override def imageCropbox = Some(ImageCropBox(Coordinate(width(138), height(299)), width(552), height(599)))
}

case object UnisexTee extends SupportedProduct(id("u-tee-slim-crew"), t("unisex-tshirts")) {
  override def configDecoder = apparelConfigDecoder

  override def imageCropbox = Some(ImageCropBox(Coordinate(width(138), height(299)), width(552), height(599)))
}

case object BaseballTee extends SupportedProduct(id("u-tee-slim-crew-baseball"), t("mens-baseball-sleeve-tshirts")) {
  override def configDecoder = apparelConfigDecoder

  override def imageCropbox = Some(ImageCropBox(Coordinate(width(138), height(299)), width(552), height(599)))
}

case object LongSleeveTee extends SupportedProduct(id("u-tee-slim-crew-long"), t("mens-longsleeve-tshirts")) {
  override def configDecoder = apparelConfigDecoder

  override def imageCropbox = Some(ImageCropBox(Coordinate(width(138), height(299)), width(552), height(599)))
}

case object Pullover extends SupportedProduct(id("u-sweatshirt-pullover"), t("pullovers")) {
  override def configDecoder = apparelConfigDecoder

  override def imageCropbox = Some(ImageCropBox(Coordinate(width(138), height(299)), width(552), height(599)))
}

case object MensVNeckTee extends SupportedProduct(id("u-tee-slim-vneck"), t("mens-vneck-tshirts")) {
  override def configDecoder = apparelConfigDecoder

  override def imageCropbox = Some(ImageCropBox(Coordinate(width(138), height(299)), width(552), height(599)))
}

case object MensGraphicTee extends SupportedProduct(id("m-tee-panel-slim-crew"), t("mens-graphic-t-shirts")) {
  override def configDecoder = apparelConfigDecoder

  override def imageCropbox = Some(ImageCropBox(Coordinate(width(138), height(299)), width(552), height(599)))
}

case object WomensFittedTee extends SupportedProduct(id("w-tee-slim-crew"), t("womens-fitted-tshirts")) {
  override def configDecoder = apparelConfigDecoder

  override def imageCropbox = Some(ImageCropBox(Coordinate(width(138), height(299)), width(552), height(599)))
}

case object WomensFittedScoopTee extends SupportedProduct(id("w-tee-slim-scoop"), t("womens-fitted-scoops")) {
  override def configDecoder = apparelConfigDecoder

  override def imageCropbox = Some(ImageCropBox(Coordinate(width(138), height(299)), width(552), height(599)))
}

case object WomensRelaxedFitTee extends SupportedProduct(id("w-tee-relaxed-crew"), t("womens-relaxed-fits")) {
  override def configDecoder = apparelConfigDecoder

  override def imageCropbox = Some(ImageCropBox(Coordinate(width(138), height(299)), width(552), height(599)))
}

case object WomensFittedVNeckTee extends SupportedProduct(id("w-tee-slim-vneck"), t("womens-fitted-v-necks")) {
  override def configDecoder = apparelConfigDecoder

  override def imageCropbox = Some(ImageCropBox(Coordinate(width(138), height(299)), width(552), height(599)))
}

case object ChiffonTop extends SupportedProduct(id("w-top-chiffon"), t("chiffon-tops")) {
  override def configDecoder = apparelConfigDecoder

  override def imageCropbox = Some(ImageCropBox(Coordinate(width(138), height(299)), width(552), height(599)))
}

case object UnisexTankTop extends SupportedProduct(id("u-tank-slim"), t("unisex-tanks")) {
  override def configDecoder = apparelConfigDecoder

  override def imageCropbox = Some(ImageCropBox(Coordinate(width(138), height(299)), width(552), height(599)))
}

case object KidsTee extends SupportedProduct(id("k-tee-crew"), t("kids-tshirts")) {
  override def configDecoder = apparelConfigDecoder

  override def imageCropbox = None
}

case object BabyLongSleeveOnePiece extends SupportedProduct(id("b-onesie-long"), t("baby-longsleeve-onepieces")) {
  override def configDecoder = apparelConfigDecoder

  override def imageCropbox = None
}

case object BabyShortSleeveOnePiece extends SupportedProduct(id("b-onesie-short"), t("onesies")) {
  override def configDecoder = apparelConfigDecoder

  override def imageCropbox = None
}

case object BabyTee extends SupportedProduct(id("b-tee-crew"), t("baby-tshirts")) {
  override def configDecoder = apparelConfigDecoder

  override def imageCropbox = None
}

case object RacerbackTank extends SupportedProduct(id("w-tank-racerback"), t("racerbacks")) {
  override def configDecoder = apparelConfigDecoder

  override def imageCropbox = Some(ImageCropBox(Coordinate(width(138), height(299)), width(552), height(599)))
}

case object ContrastTank extends SupportedProduct(id("w-tank-panel-contrast"), t("contrast-tanks")) {
  override def configDecoder = apparelConfigDecoder

  override def imageCropbox = Some(ImageCropBox(Coordinate(width(138), height(299)), width(552), height(599)))
}

case object LightweightHoodie extends SupportedProduct(id("u-sweatshirt-lightweight-hooded"), t("lightweight-hoodies")) {
  override def configDecoder = apparelConfigDecoder

  override def imageCropbox = Some(ImageCropBox(Coordinate(width(138), height(299)), width(552), height(599)))
}

case object LightweightSweatshirt extends SupportedProduct(id("u-sweatshirt-lightweight-raglan"), t("lightweight-raglan-sweatshirts")) {
  override def configDecoder = apparelConfigDecoder

  override def imageCropbox = Some(ImageCropBox(Coordinate(width(138), height(299)), width(552), height(599)))
}

case object PulloverHoodie extends SupportedProduct(id("u-sweatshirt-hooded"), t("pullover-hoodies")) {
  override def configDecoder = apparelConfigDecoder

  override def imageCropbox = Some(ImageCropBox(Coordinate(width(138), height(299)), width(552), height(599)))
}

case object ZippedHoodie extends SupportedProduct(id("u-sweatshirt-zip-hooded"), t("zipped-hoodies")) {
  override def configDecoder = apparelConfigDecoder

  override def imageCropbox = Some(ImageCropBox(Coordinate(width(138), height(299)), width(552), height(599)))
}

case object GraphicTeeDress extends SupportedProduct(id("w-dress-panel-tee"), t("graphic-t-shirt-dresses")) {
  override def configDecoder = apparelConfigDecoder

  override def imageCropbox = Some(ImageCropBox(Coordinate(width(138), height(299)), width(552), height(599)))
}

case object ALineDress extends SupportedProduct(id("w-dress-trapeze-a-line"), t("a-line-dresses")) {
  override def configDecoder = apparelConfigDecoder

  override def imageCropbox = Some(ImageCropBox(Coordinate(width(138), height(299)), width(552), height(599)))
}

case object MiniSkirt extends SupportedProduct(id("w-skirt-mini"), t("pencil-skirts")) {
  override def configDecoder = miniSkirtConfigDecoder

  override def imageCropbox = None
}

case object Leggings extends SupportedProduct(id("w-leggings"), t("leggings")) {
  override def configDecoder = leggingsConfigDecoder

  override def imageCropbox = None
}

case object Scarf extends SupportedProduct(id("w-scarf"), t("scarves")) {
  override def configDecoder = scarfDecoder

  override def imageCropbox = None
}

// Home decor
case object ThrowPillow extends SupportedProduct(id("u-pillow-throw"), t("throw-pillows")) {
  override def configDecoder = throwPillowDecoder

  override def imageCropbox = None
}

case object DuvetCover extends SupportedProduct(id("u-duvet"), t("duvet-covers")) {
  override def configDecoder = duvetConfigDecoder

  override def imageCropbox = None
}

case object Mug extends SupportedProduct(id("u-mug-regular"), t("standard-mugs")) {
  override def configDecoder = mugDecoder

  override def imageCropbox = None
}

case object TallMug extends SupportedProduct(id("u-mug-tall"), t("tall mug")) {
  override def configDecoder = mugDecoder

  override def imageCropbox = None
}

case object TravelMug extends SupportedProduct(id("u-mug-travel"), t("travel-mugs")) {
  override def configDecoder = mugDecoder

  override def imageCropbox = None
}

case object AcrylicBlock extends SupportedProduct(id("u-block-acrylic"), t("acrylic-blocks")) {
  override def configDecoder = acrylicBlockDecoder

  override def imageCropbox = None
}

case object WallClock extends SupportedProduct(id("u-clock"), t("clocks")) {
  override def configDecoder = wallClockConfigDecoder

  override def imageCropbox = None
}

// Wall art
case object Poster extends SupportedProduct(id("u-print-poster"), t("posters")) {
  override def configDecoder = artPrintConfigDecoder

  override def imageCropbox = None
}

case object ArtPrint extends SupportedProduct(id("u-print-art"), t("art-prints")) {
  override def configDecoder = artPrintConfigDecoder

  override def imageCropbox = None
}

case object CanvasPrint extends SupportedProduct(id("u-print-canvas"), t("canvas-prints")) {
  override def configDecoder = artPrintConfigDecoder

  override def imageCropbox = None
}

case object FramedPrint extends SupportedProduct(id("u-print-frame"), t("framed-prints")) {
  override def configDecoder = framedPrintConfigDecoder

  override def imageCropbox = None
}

case object MetalPrint extends SupportedProduct(id("u-print-metal"), t("metal-prints")) {
  override def configDecoder = printConfigDecoder

  override def imageCropbox = None
}

case object PhotographicPrint extends SupportedProduct(id("u-print-photo"), t("photographic-prints")) {
  override def configDecoder = printConfigDecoder

  override def imageCropbox = None
}

case object Tapestry extends SupportedProduct(id("u-print-tapestry"), t("tapestry")) {
  override def configDecoder = tapestryDecoder

  override def imageCropbox = None
}

case object GalleryBoard extends SupportedProduct(id("u-print-board-gallery"), t("gallery-boards")) {
  override def configDecoder = galleryBoardConfigDecoder

  override def imageCropbox = None
}

// Stationery
case object HardcoverJournal extends SupportedProduct(id("u-notebook-hardcover"), t("hardcover-journals")) {
  override def configDecoder = journalConfigDecoder

  override def imageCropbox = None
}

case object SpiralNotebook extends SupportedProduct(id("u-notebook-spiral"), t("spiral-notebooks")) {
  override def configDecoder = journalConfigDecoder

  override def imageCropbox = None
}

case object GreetingCard extends SupportedProduct(id("u-card-greeting"), t("greeting-cards")) {
  override def configDecoder = greetingCardConfigDecoder

  override def imageCropbox = None
}

case object PostCard extends SupportedProduct(id("u-card-post"), t("postcards")) {
  override def configDecoder = greetingCardConfigDecoder

  override def imageCropbox = None
}

case object Sticker extends SupportedProduct(id("u-sticker"), t("stickers")) {
  override def configDecoder = stickerDecoder

  override def imageCropbox = None
}

// Bags
case object DrawstringBag extends SupportedProduct(id("u-bag-drawstring"), t("drawstring-bags")) {
  override def configDecoder = basicDecoder

  override def imageCropbox = None
}

case object ToteBag extends SupportedProduct(id("u-bag-tote"), t("tote-bags")) {
  override def configDecoder = toteBagDecoder

  override def imageCropbox = None
}

case object StudioPouch extends SupportedProduct(id("u-bag-studiopouch"), t("pouches")) {
  override def configDecoder = studioPouchDecoder

  override def imageCropbox = None
}

// Cases & skins
case object iPhoneCase extends SupportedProduct(id("u-case-iphone"), t("iphone-cases")) {
  override def configDecoder = phoneCaseConfigDecoder

  override def imageCropbox = None
}

case object iPhoneWallet extends SupportedProduct(id("u-case-wallet-iphone"), t("iphone-wallets")) {
  override def configDecoder = phoneCaseConfigDecoder

  override def imageCropbox = None
}

case object GalaxyCase extends SupportedProduct(id("u-case-samsung"), t("samsung-galaxy-cases")) {
  override def configDecoder = phoneCaseConfigDecoder

  override def imageCropbox = None
}

case object LaptopSkin extends SupportedProduct(id("u-case-laptop-skin"), t("laptop-skins")) {
  override def configDecoder = deviceCaseDecoder

  override def imageCropbox = None
}

case object LaptopSleeve extends SupportedProduct(id("u-case-laptop-sleeve"), t("laptop-sleeves")) {
  override def configDecoder = sleeveDecoder

  override def imageCropbox = None
}

object SupportedProduct {
  // Note. We need lazy here as there is some initiation order interaction between the isSupported function and the
  //       sealedInstancesOf macro that means we get NPEs occasionally.
  lazy val supportedProducts: Seq[SupportedProduct] = sealedInstancesOf[SupportedProduct]

  def isSupported(product: Product): Boolean = supportedProducts.exists(sp => sp.typeId == product.definition.typeId)

  def supportedProductForProduct(product: Product): Option[SupportedProduct] =
    supportedProducts.find(sp => sp.typeId == product.definition.typeId)

  def addCropBox(product: Product): Product =
    supportedProductForProduct(product).flatMap(sp => sp.imageCropbox).map { cb =>
      product.copy(images = product.images.map(i => i.copy(cropBox = Some(cb))))
    }.getOrElse(product)

  private[product] def id(t: String): ProductTypeId = ProductTypeId(t)

  private[product] def t(t: String): ProductSearchTerm = ProductSearchTerm(t)
}
