package com.redbubble.gql.backends.product

import com.redbubble.gql.product._
import com.redbubble.gql.util.spec.ModelGenerators
import com.redbubble.util.io.Resource._
import com.redbubble.util.spec.SpecHelper
import com.twitter.io.Buf
import org.scalacheck.Prop._
import org.scalacheck.Properties
import org.specs2.mutable.Specification

final class ProductDecodersSpec extends Specification with SpecHelper with ModelGenerators with ProductApiJson {

  //
  // Apparel products, covers most of our products.
  //

  val availableProductsDecodingProp = new Properties("Available products decoding") {
    property("decoding") = forAll(genApparelProduct) { (product: Product) =>
      val json = tshirtProductJson(product)
      val decoded = decode(json)(ProductDecoders.productsDataDecoder)
      decoded must beRight(Seq(product))
    }
  }

  s2"Decoding available products$availableProductsDecodingProp"

  //
  // Framed prints
  //

  val framedPrintDecodingProp = new Properties("Framed print decoding") {
    property("decoding") = forAll(genFramedPrintProduct) { (product: Product) =>
      val json = framedPrintProductJson(product)
      val decoded = decode(json)(ProductDecoders.productsDataDecoder)
      decoded must beRight(Seq(product))
    }
  }

  s2"Decoding framed prints$framedPrintDecodingProp"

  //
  // iPhone Cases & wallets, Samsung cases.
  //

  val samsungGalaxyCaseDecodingProp = new Properties("Samsung galaxy phone case decoding") {
    property("decoding") = forAll(genPhoneCaseProduct) { (product: Product) =>
      val json = samsungGalaxyCaseProductJson(product)
      val decoded = decode(json)(ProductDecoders.productsDataDecoder)
      decoded must beRight(Seq(product))
    }
  }

  s2"Decoding samsung galaxy cases$samsungGalaxyCaseDecodingProp"

  //
  // Other products (general catch-all)
  //

  lazy val lotsOfProductsJson = classpathResourceAsBuf("/available_product_lots_products.json").get
  lazy val andMoreProductsJson = classpathResourceAsBuf("/available_product_more_products.json").get
  lazy val mainlyCasesProductsJson = classpathResourceAsBuf("/available_products_mostly_cases.json").get
  lazy val cardsProductsJson = classpathResourceAsBuf("/available_products_cards.json").get
  lazy val galleryBoardsProductsJson = classpathResourceAsBuf("/available_products_galleryboard.json").get
  lazy val acrylicBlockProductsJson = classpathResourceAsBuf("/available_product_acrylic_block.json").get

  "Decoding all available products for a work" >> {
    "mainly cases" >> {
      decodeProducts(mainlyCasesProductsJson, 13)
    }
    "mainly cards" >> {
      decodeProducts(cardsProductsJson, 9)
    }
    "gallery boards" >> {
      decodeProducts(galleryBoardsProductsJson, 56)
    }
    "acrylic blocks" >> {
      decodeProducts(acrylicBlockProductsJson, 37)
    }
    "most (currently supported) products" >> {
      decodeProducts(lotsOfProductsJson, 58)
    }
    "more products, again" >> {
      decodeProducts(andMoreProductsJson, 58)
    }
  }

  private def decodeProducts(json: Buf, expectedSize: Int) = {
    val decoded = decode(json)(ProductDecoders.productsDataDecoder)
    decoded must beRight { (ps: Seq[Product]) =>
      ps must haveSize(expectedSize)
    }
  }
}
