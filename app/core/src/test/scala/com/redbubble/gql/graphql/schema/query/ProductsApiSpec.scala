package com.redbubble.gql.graphql.schema.query

import com.redbubble.graphql.SuccessfulGraphQlResult
import com.redbubble.gql.graphql.schema.QueryHelper
import com.redbubble.util.spec.SpecHelper
import org.specs2.mutable.Specification

final class ProductsApiSpec extends Specification with SpecHelper with QueryHelper {
  val categoriesQuery =
    s"""
       |{
       |$productCategoriesFields
       |}
     """.stripMargin

  "Product categories" >> {
    "can be queried" >> {
      val result = testQuery(categoriesQuery)
      result must beAnInstanceOf[SuccessfulGraphQlResult]
    }
  }

  // This work has a lot (52 at time of writing) available products.
  val workId = "21703864"
  val availableProductsQuery =
    s"""
       |{
       |  relatedWorks(workId: "$workId", currency: "AUD", country: "AU") {
       |    $workFields
       |    availableProducts(country: "AU", currency: "AUD") {
       |      $productFields
       |    }
       |  }
       |}
    """.stripMargin

  "Available products" >> {
    "can be queried" >> {
      val result = testQuery(availableProductsQuery)
      result must beAnInstanceOf[SuccessfulGraphQlResult]
    }
  }

  val productDetailsQuery =
    s"""
       |{
       |  productDetails(
       |    workId: "$workId",
       |    country: "AU",
       |    currency: "AUD",
       |    selected: {
       |    	typeId: "u-tee-slim-crew",
       |      selectedConfig: [
       |        {key: "size", value: "small"},
       |        {key: "body_color", value: "black"},
       |        {key: "print_only_front", value: "false"},
       |        {key: "on_back", value: "false"},
       |      ],
       |    }
       |  ) {
       |    $productFields
       |  }
       |}
    """.stripMargin

  "Product details" >> {
    "can be retrieved" >> {
      val result = testQuery(productDetailsQuery)
      result must beAnInstanceOf[SuccessfulGraphQlResult]
    }
  }
}
