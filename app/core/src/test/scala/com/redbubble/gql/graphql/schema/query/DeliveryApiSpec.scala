package com.redbubble.gql.graphql.schema.query

import com.redbubble.graphql.SuccessfulGraphQlResult
import com.redbubble.gql.graphql.schema.QueryHelper
import com.redbubble.util.spec.SpecHelper
import org.specs2.mutable.Specification

final class DeliveryApiSpec extends Specification with SpecHelper with QueryHelper {
  private val deliveryDates =
    s"""
       |{
       |  deliveryDates(productName: "mens-t-shirts", country:"AU") {
       |    lowStandard
       |    highStandard
       |    lowExpress
       |    highExpress
       |  }
       |}
    """.stripMargin

  private val notAValidProductDeliveryDates =
    s"""
       |{
       |  deliveryDates(productName: "tee", country:"AU") {
       |    lowStandard
       |    highStandard
       |    lowExpress
       |    highExpress
       |  }
       |}
    """.stripMargin

  "Products" >> {
    "can have their dates of delivery found" >> {
      val result = testQuery(deliveryDates)
      result must beAnInstanceOf[SuccessfulGraphQlResult]
    }
  }

  "Invalid products" >> {
    "cannot have their dates of delivery found" >> {
      val result = testQuery(notAValidProductDeliveryDates)
      result must beAnInstanceOf[SuccessfulGraphQlResult]
    }
  }
}
