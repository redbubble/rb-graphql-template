package com.redbubble.gql.graphql.schema.query

import com.redbubble.graphql.SuccessfulGraphQlResult
import com.redbubble.gql.graphql.schema.QueryHelper
import com.redbubble.util.spec.SpecHelper
import org.specs2.mutable.Specification

final class SearchApiSpec extends Specification with SpecHelper with QueryHelper {
  val productSearchQuery =
    s"""
       |{
       |  search(keywords: "cat tee", country: "AU", currency: "AUD") {
       |    totalProducts
       |    products {
       |      $productFields
       |    }
       |    pagination {
       |      $paginationFields
       |    }
       |  }
       |}
    """.stripMargin

  "Products" >> {
    "can be found" >> {
      val result = testQuery(productSearchQuery)
      result must beAnInstanceOf[SuccessfulGraphQlResult]
    }
  }
}
