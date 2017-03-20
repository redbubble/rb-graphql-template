package com.redbubble.gql.product

import com.redbubble.gql.services.product._
import com.redbubble.gql.util.spec.ModelGenerators
import com.redbubble.util.spec.SpecHelper
import org.scalacheck.Prop._
import org.scalacheck.Properties
import org.specs2.mutable.Specification

final class SupportedProductSpec extends Specification with SpecHelper with ModelGenerators {
  val supportedProducts = new Properties("Supported products") {
    property("supported") = forAll(genSupportedProductProduct) { (p: Product) =>
      SupportedProduct.isSupported(p) must beTrue
    }

    // This will fail if a supported product hasn't been added to any categories. Go add it.
    property("categories") = forAll(genSupportedProductProduct) { (p: Product) =>
      p.categories must not(beEmpty)
    }
  }

  s2"Supported products are supported$supportedProducts"

  private val genUnsupportedId = genNonEmptyString.map(ProductTypeId)

  val unsupportedProducts = new Properties("Unsupported products") {
    property("unsupported") = forAll(genProduct, genUnsupportedId) { (p: Product, id: ProductTypeId) =>
      val unsupported = p.copy(definition = ProductDefinition(id, p.definition.displayName))
      SupportedProduct.isSupported(unsupported) must beFalse
    }
  }

  s2"Unsupported products are not supported$unsupportedProducts"
}
