package com.redbubble.gql.graphql.schema.query

import com.redbubble.graphql.SuccessfulGraphQlResult
import com.redbubble.gql.graphql.schema.QueryHelper
import com.redbubble.util.spec.SpecHelper
import org.specs2.mutable.Specification

class TopicsApiSpec extends Specification with SpecHelper with QueryHelper {
  val worksForTopicsQuery: String =
    s"""
       |{
       |  worksForTopics(topics: ["tropical", "palm tree"], country: "AU", currency: "AUD", limit: 48, offset: 0) {
       |   $workFields
       |  }
       |}
     """.stripMargin

  "Topics" >> {
    "can have multiple works for a topic" >> {
      val result = testQuery(worksForTopicsQuery)
      result must beAnInstanceOf[SuccessfulGraphQlResult]
    }
  }
}
