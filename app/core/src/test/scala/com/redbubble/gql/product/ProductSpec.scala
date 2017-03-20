package com.redbubble.gql.product

import com.redbubble.gql.services.product._
import com.redbubble.gql.util.spec.ModelGenerators
import com.redbubble.util.spec.SpecHelper
import org.specs2.mutable.Specification

final class ProductSpec extends Specification with SpecHelper with ModelGenerators {
  private val templateProduct = sample(genProduct)

  "A product's categories can be found" >> {
    "for apparel" >> {
      product(UnisexTee.typeId).categories must beEqualTo(Seq(Mens, Womens))
      product(ClassicTee.typeId).categories must beEqualTo(Seq(Mens))
    }
  }

  private def product(typeId: ProductTypeId) =
    templateProduct.copy(definition = ProductDefinition(typeId, templateProduct.definition.displayName))
}
