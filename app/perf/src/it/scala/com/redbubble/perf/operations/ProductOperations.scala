package com.redbubble.perf.operations

import com.redbubble.gql.services.product._
import com.redbubble.perf.common.Graphql._
import com.redbubble.perf.queries.ProductQueries._
import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder

trait ProductOperations {
  final def availableProducts(workId: WorkId): ChainBuilder = {
    exec(
      graphqlRequest("Available Products", graphQlQueryBody(availableProductsQuery(workId)))
    )
  }

  final val productCategories: ChainBuilder = {
    exec(
      graphqlRequest("Product Categories", graphQlQueryBody(productCategoriesQuery))
    )
  }
}
