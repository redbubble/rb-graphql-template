package com.redbubble.gql.graphql.schema.query

import com.redbubble.graphql.SuccessfulGraphQlResult
import com.redbubble.gql.graphql.schema.QueryHelper
import com.redbubble.util.spec.SpecHelper
import org.specs2.mutable.Specification

final class WorksApiSpec extends Specification with SpecHelper with QueryHelper {
  val workDetailsQuery: String =
    s"""
       |{
       |  workDetails(workId: "11421523", country:"AU", currency: "AUD") {
       |    $workFields
       |  }
       |}
    """.stripMargin

  val relatedWorksQuery: String =
    s"""
       |{
       |  relatedWorks(workId: "11421523", country:"AU", currency: "AUD") {
       |    $workFields
       |  }
       |}
    """.stripMargin

  "Works" >> {
    "can have related works found" >> {
      val result = testQuery(workDetailsQuery)
      result must beAnInstanceOf[SuccessfulGraphQlResult]
    }

    "can have their details found" >> {
      val result = testQuery(relatedWorksQuery)
      result must beAnInstanceOf[SuccessfulGraphQlResult]
    }
  }
}
