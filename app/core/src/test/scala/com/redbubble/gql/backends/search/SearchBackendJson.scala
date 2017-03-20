package com.redbubble.gql.backends.search

import com.redbubble.gql.backends.product.ProductApiJson
import com.redbubble.gql.product.Product
import com.redbubble.util.io.Resource._
import com.twitter.io.Buf

trait SearchBackendJson extends ProductApiJson {
  final lazy val searchJson: Buf = classpathResourceAsBuf("/search_result.json").get

  def productSearchJson(result: RawSearchResult): String =
    s"""
       |{
       |  "data": {
       |    "products": [
       |      ${result.products.map(searchProductJson).mkString(",")}
       |    ],
       |    "total_pages": ${result.metadata.totalPages},
       |    "results_per_page": ${result.metadata.resultsPerPage},
       |    "current_page": ${result.metadata.currentPage},
       |    "total_products": ${result.metadata.totalProducts},
       |    "raw_query_string": "${result.metadata.keywords}"
       |  }
       |}
     """.stripMargin

  def searchProductJson(product: Product): String =
    s"""
       |{
       |  "id": "${product.id}",
       |  "work_id": ${product.workId},
       |  "product_info": [
       |    ${product.info.map(productInfoJson).mkString(",")}
       |   ],
       |  "ia_code": "${product.definition.typeId}",
       |  "display_name": "${product.definition.displayName}",
       |  "product_image": ${productImageJson(product.images.head)},
       |  "product_images": [
       |     ${product.images.map(productImageJson).mkString(",")}
       |   ],
       |  "product_page": "${product.webLink.toString}",
       |  "price": {
       |    "currency": "${product.price.currency.isoCode}",
       |    "amount": "${product.price.amount.toString}"
       |  }
       |}
     """.stripMargin
}
