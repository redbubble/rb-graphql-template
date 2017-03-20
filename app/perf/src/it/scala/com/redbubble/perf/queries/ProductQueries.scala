package com.redbubble.perf.queries

import com.redbubble.gql.services.product._

//noinspection ScalaStyle
trait ProductQueries {
  def availableProductsQuery(workId: WorkId): String =
    s"""
       |{
       |  availableProducts(workId: "$workId", currency: "AUD", country: "AU") {
       |    workId
       |    price {
       |      amount
       |      currency
       |      prefix
       |      unit
       |    }
       |    images {
       |      url
       |      size {
       |        width
       |        height
       |      }
       |    }
       |    typeId
       |    typeName
       |    id
       |    webLink
       |    categories {
       |      name
       |    }
       |    genericConfig {
       |      availableColours {
       |        name
       |        label
       |        value
       |        valueDisplayName
       |        defaultSelection
       |        hexValue
       |      }
       |      availableSizes {
       |        name
       |        label
       |        value
       |        valueDisplayName
       |        defaultSelection
       |        measurement {
       |          ... on ApparelSizeMeasurement {
       |          label
       |          chest
       |          length
       |          }
       |        }
       |      }
       |      addnConfig {
       |        name
       |        label
       |        value
       |        valueDisplayName
       |        defaultSelection
       |      }
       |    }
       |  }
       |}
     """.stripMargin

  val productCategoriesQuery: String =
    s"""
       |{
       |	categories {
       |    name
       |    products {
       |      typeId
       |    }
       |  }
       |}
     """.stripMargin
}

object ProductQueries extends ProductQueries
