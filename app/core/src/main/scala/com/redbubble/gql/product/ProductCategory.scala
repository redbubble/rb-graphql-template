package com.redbubble.gql.product

import cats.data.NonEmptyList
import com.redbubble.gql.product.ProductCategory.n

/**
  * Categories which products belong in. Products can be in more than one category.
  *
  * Note. This is modeled around the top level UX from the website, but differs in small ways to be more internally
  * consistent.
  *
  * @param name The human readable (display) name of the category.
  */
sealed abstract class ProductCategory(val name: CategoryName) {
  def products: NonEmptyList[SupportedProduct]
}

// TODO These should be in the supported products, not here, then it's all in the one place and these can just delegate.
case object Mens extends ProductCategory(n("Men's")) {
  override val products: NonEmptyList[SupportedProduct] = NonEmptyList.of(
    UnisexTee,
    ClassicTee,
    TriblendTee,
    MensGraphicTee,
    MensVNeckTee,
    LongSleeveTee,
    BaseballTee,
    UnisexTankTop,
    LightweightHoodie,
    PulloverHoodie,
    ZippedHoodie,
    LightweightSweatshirt,
    Pullover
  )
}

case object Womens extends ProductCategory(n("Women's")) {
  val products: NonEmptyList[SupportedProduct] = NonEmptyList.of(
    UnisexTee,
    ChiffonTop,
    ContrastTank,
    ALineDress,
    GraphicTeeDress,
    WomensFittedScoopTee,
    WomensFittedVNeckTee,
    WomensRelaxedFitTee,
    WomensFittedTee,
    RacerbackTank,
    UnisexTankTop,
    LightweightHoodie,
    PulloverHoodie,
    ZippedHoodie,
    LightweightSweatshirt,
    Leggings,
    Scarf,
    MiniSkirt,
    Leggings
  )
}

case object Kids extends ProductCategory(n("Kids")) {
  val products: NonEmptyList[SupportedProduct] = NonEmptyList.of(
    KidsTee,
    BabyTee,
    BabyShortSleeveOnePiece,
    BabyLongSleeveOnePiece
  )
}

case object CasesAndSkins extends ProductCategory(n("Cases & Skins")) {
  val products: NonEmptyList[SupportedProduct] = NonEmptyList.of(
    iPhoneCase,
    iPhoneWallet,
    GalaxyCase,
    LaptopSkin,
    LaptopSleeve
  )
}

case object Stickers extends ProductCategory(n("Stickers")) {
  val products: NonEmptyList[SupportedProduct] = NonEmptyList.of(
    Sticker
  )
}

case object WallArt extends ProductCategory(n("Wall Art")) {
  val products: NonEmptyList[SupportedProduct] = NonEmptyList.of(
    Poster,
    CanvasPrint,
    PhotographicPrint,
    ArtPrint,
    FramedPrint,
    MetalPrint,
    Tapestry,
    GalleryBoard
  )
}

case object HomeDecor extends ProductCategory(n("Home Decor")) {
  val products: NonEmptyList[SupportedProduct] = NonEmptyList.of(
    ThrowPillow,
    DuvetCover,
    Mug,
    TallMug,
    TravelMug,
    Tapestry,
    AcrylicBlock,
    WallClock
  )
}

case object Stationery extends ProductCategory(n("Stationery")) {
  val products: NonEmptyList[SupportedProduct] = NonEmptyList.of(
    GreetingCard,
    PostCard,
    SpiralNotebook,
    HardcoverJournal
  )
}

case object Bags extends ProductCategory(n("Bags")) {
  val products: NonEmptyList[SupportedProduct] = NonEmptyList.of(
    ToteBag,
    StudioPouch,
    DrawstringBag,
    LaptopSleeve
  )
}

object ProductCategory {
  // Note. We explicitly list these here rather than retrieve them via macro as we want a specific order.
  val topLevelCategories: NonEmptyList[ProductCategory] = NonEmptyList.of(
    Mens,
    Womens,
    Kids,
    CasesAndSkins,
    Stickers,
    WallArt,
    HomeDecor,
    Stationery,
    Bags
  )

  private[product] def n(n: String): CategoryName = CategoryName(n)
}
