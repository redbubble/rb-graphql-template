package com.redbubble.gql.graphql.schema.mutation

import com.redbubble.graphql.SuccessfulGraphQlResult
import com.redbubble.gql.graphql.schema.QueryHelper
import com.redbubble.util.spec.SpecHelper
import org.specs2.mutable.Specification

final class CartSpec extends Specification with SpecHelper with QueryHelper {
  val cartBulkAddMutation =
    s"""
       |mutation {
       |  cart: bulkAddToCart(
       |    input: {
       |      products: [
       |        {
       |          workId: "23669162",
       |          typeId: "w-dress-panel-tee",
       |          quantity: 3,
       |          config: [
       |            {key: "size", value: "large"},
       |            {key: "body_color", value: "black"}
       |          ]
       |        }
       |      ]
       |    }
       |  ) {
       |    $sessionFields
       |  }
       |}
    """.stripMargin

  val cartBulkAddWithLocaleMutation =
    s"""
       |mutation {
       |  cart: bulkAddToCartWithLocale(
       |    input: {
       |      products: [
       |        {
       |          workId: "23669162",
       |          typeId: "w-dress-panel-tee",
       |          quantity: 3,
       |          config: [
       |            {key: "size", value: "large"},
       |            {key: "body_color", value: "black"}
       |          ]
       |        }
       |      ]
       |    },
       |    country:"AU",
       |    currency: "AUD",
       |    language: "en"
       |  ) {
       |    $sessionFields
       |  }
       |}
    """.stripMargin

  "Carts" >> {
    "can have products added to them" >> {
      val result = testQuery(cartBulkAddMutation)
      result must beAnInstanceOf[SuccessfulGraphQlResult]
    }
    "can have products added to them with Locale" >> {
      val result = testQuery(cartBulkAddWithLocaleMutation)
      result must beAnInstanceOf[SuccessfulGraphQlResult]
    }
  }
}
