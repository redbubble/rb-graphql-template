package com.redbubble.gql.product

import com.redbubble.gql.services.product._
import com.redbubble.gql.util.spec.ModelGenerators
import com.redbubble.util.io.Resource._
import com.redbubble.util.spec.SpecHelper
import com.twitter.io.Buf
import org.specs2.mutable.Specification

final class FeaturedProductsSpec extends Specification with SpecHelper with ModelGenerators {
  lazy val lotsOfProductsJson = classpathResourceAsBuf("/available_product_lots_products.json").get
  lazy val mainlyCasesProductsJson = classpathResourceAsBuf("/available_products_mostly_cases.json").get
  lazy val cardsProductsJson = classpathResourceAsBuf("/available_products_cards.json").get
  lazy val galleryBoardsProductsJson = classpathResourceAsBuf("/available_products_galleryboard.json").get
  lazy val acrylicBlockProductsJson = classpathResourceAsBuf("/available_product_acrylic_block.json").get

  "Featured products for a work" >> {
    "mainly cases" >> {
      checkFeaturedProducts(mainlyCasesProductsJson)
    }
    "mainly cards" >> {
      checkFeaturedProducts(cardsProductsJson)
    }
    "gallery boards" >> {
      checkFeaturedProducts(galleryBoardsProductsJson)
    }
    "acrylic blocks" >> {
      checkFeaturedProducts(acrylicBlockProductsJson)
    }
    "most (currently supported) products" >> {
      checkFeaturedProducts(lotsOfProductsJson)
    }
  }

  private def checkFeaturedProducts(json: Buf) = {
    val decoded = decode(json)(ProductDecoders.productsDataDecoder)
    decoded must beRight { (products: Seq[Product]) =>
      val featured = FeaturedProducts.filterFeatured(WorkId("21703864"), products)
      featured.totalProducts must beEqualTo(products.length)
      featured.featured must not(beEmpty)
    }
  }
}
