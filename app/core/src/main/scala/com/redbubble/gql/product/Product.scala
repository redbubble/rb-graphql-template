package com.redbubble.gql.product

import java.net.URL

import com.redbubble.gql.core._
import com.redbubble.gql.product.ProductCategory.topLevelCategories
import com.redbubble.gql.product.config.ProductConfiguration
import com.redbubble.gql.services.product._

/**
  * These are the core identifiers for a product.
  */
final case class ProductDefinition(typeId: ProductTypeId, displayName: ProductDisplayName)

/**
  * A product for sale, which is a combination of an artwork, and an actual physical item.
  *
  * We decouple work & products here using `workId` rather than adding a work directly. We do this mainly as we do
  * not have a work details API to look up the details of a work when requesting `availableProducts` from the GQL
  * API (and to be fair, we have no need for that at present). Use `WorkWithProducts` to represent a work/products
  * pairing if you need that.
  *
  */
final case class Product(productId: ProductId, workId: WorkId, definition: ProductDefinition, info: ProductInfo,
    config: Option[ProductConfiguration], price: Price, webLink: URL, images: Seq[Image]) extends Identifiable {
  override val id = Id(productId)

  lazy val categories: Seq[ProductCategory] =
    topLevelCategories.filter(c => c.products.exists(sp => sp.typeId == definition.typeId))

  /**
    * This product's config, in a generic format. This is a non-typesafe, least specific way to get the
    * config back for a product. The only useful thing you get is colours, sizes and a flattened version of the options
    * (`ProductConfiguration.allConfig`).
    */
  lazy val genericConfig: Option[ProductConfiguration] = config
}
