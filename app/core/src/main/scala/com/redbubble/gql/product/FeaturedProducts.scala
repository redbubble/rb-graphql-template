package com.redbubble.gql.product

import cats.data.NonEmptyList
import com.redbubble.gql.services.product._
import com.redbubble.util.data.syntax._

import scala.util.Random

final case class FeaturedProducts(workId: WorkId, featured: Seq[Product], totalProducts: Int)

/**
  * Feature products, using roughly the algorithm defined here:
  * https://docs.google.com/spreadsheets/d/1ZFeEVej3-67ADzqp_YHvITjE2wso_YkXmi4JAJRVZWQ/edit#gid=0
  */
object FeaturedProducts {
  private val NumberToFeature = 6
  private val FillerPadding = 6
  private val NoCategory = CategoryName("no_category")

  // Products that we never want to feature for some reason, e.g. poor quality images.
  private val blacklisted: Seq[SupportedProduct] = Seq(
    BabyLongSleeveOnePiece,
    BabyShortSleeveOnePiece,
    BabyTee
  )

  // Set up the featured products. Because we look them up by category each of these lists must map 1-1 with a category
  // i.e. each product in the list must belong to the category we're finding for it below in `buildFeatured`.
  private val mensFeatured: NonEmptyList[SupportedProduct] = NonEmptyList.of(
    UnisexTee,
    PulloverHoodie,
    MensGraphicTee,
    TriblendTee
  )
  private val womensFeatured: NonEmptyList[SupportedProduct] = NonEmptyList.of(
    WomensRelaxedFitTee,
    ALineDress,
    MiniSkirt
  )
  private val wallArtFeatured: NonEmptyList[SupportedProduct] = NonEmptyList.of(
    FramedPrint,
    Poster
  )
  private val stickerFeatured: NonEmptyList[SupportedProduct] = NonEmptyList.of(
    Sticker
  )
  private val homeDecorFeatured: NonEmptyList[SupportedProduct] = NonEmptyList.of(
    ThrowPillow,
    Mug,
    WallClock
  )
  private val stationeryFeatured: NonEmptyList[SupportedProduct] = NonEmptyList.of(
    SpiralNotebook,
    HardcoverJournal
  )
  private val casesAndSkinsFeatured: NonEmptyList[SupportedProduct] = NonEmptyList.of(
    iPhoneCase,
    iPhoneWallet,
    LaptopSleeve
  )
  private val bagsFeatured: NonEmptyList[SupportedProduct] = NonEmptyList.of(
    ToteBag,
    StudioPouch
  )

  def filterFeatured(workId: WorkId, products: Seq[Product]): FeaturedProducts =
    FeaturedProducts(workId, buildFeatured(products), products.length)

  /**
    * We build featured products by:
    * - Selecting a single product from each of the featured categories;
    * - Padding out the featured products with filler products, these are taken randomly from the available
    * products for that work (excluding those already featured).
    * - For variety, we want a small number of products other than featured ones, so we add a few more
    * ([[FillerPadding]]).
    * - We then randomise all of these products and take the first [[NumberToFeature]]. Our goals are to not always put
    * the same product in the same position (e.g. a men's shirt first), and to mix up featured products and fillers,
    * as there are cases where the category is small (e.g. phone cases), which leads to a higher likelihood of always
    * seeing a phone case in the featured list for every product. We want to show variety so we mix it up a bit more.
    */
  private def buildFeatured(products: Seq[Product]): Seq[Product] = {
    val groupedByCategory = products.groupBy(p => p.categories.headOption.map(_.name).getOrElse(NoCategory))
    val featured = randomFeaturedProducts(groupedByCategory.get(Mens.name), mensFeatured) ++
        randomFeaturedProducts(groupedByCategory.get(Womens.name), womensFeatured) ++
        randomFeaturedProducts(groupedByCategory.get(WallArt.name), wallArtFeatured) ++
        randomFeaturedProducts(groupedByCategory.get(Stickers.name), stickerFeatured) ++
        randomFeaturedProducts(groupedByCategory.get(CasesAndSkins.name), casesAndSkinsFeatured) ++
        randomFeaturedProducts(groupedByCategory.get(Bags.name), bagsFeatured) ++
        randomFeaturedProducts(groupedByCategory.get(HomeDecor.name), homeDecorFeatured) ++
        randomFeaturedProducts(groupedByCategory.get(Stationery.name), stationeryFeatured)
    val numberOfFillers = NumberToFeature - featured.length + FillerPadding
    val fillers = products.filterNot(featured.toSet).filterNot(isBlacklisted).randomSlice(numberOfFillers)
    Random.shuffle(featured ++ fillers).take(NumberToFeature).distinct
  }

  // Note. We use a pattern match here to avoid creating too many lists.
  private def randomFeaturedProducts(
      available: Option[Seq[Product]], toFeature: NonEmptyList[SupportedProduct]): Seq[Product] = {
    available match {
      case None => Nil
      case Some(as) => availableFeaturedProducts(as, toFeature) match {
        case Nil => Nil
        case ps => ps.randomSlice(1)
      }
    }
  }

  private def isBlacklisted(product: Product): Boolean =
    blacklisted.exists(sp => sp.typeId == product.definition.typeId)

  // Note. This may return an empty list, if this work has no products that we can feature.
  private def availableFeaturedProducts(
      available: Seq[Product], toFeature: NonEmptyList[SupportedProduct]): Seq[Product] =
    available.filter(p => toFeature.exists(sp => sp.typeId == p.definition.typeId))
}
