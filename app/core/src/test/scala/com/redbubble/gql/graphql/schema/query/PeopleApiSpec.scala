package com.redbubble.gql.graphql.schema.query

import com.redbubble.gql.graphql.schema.QueryHelper
import com.redbubble.graphql.SuccessfulGraphQlResult
import com.redbubble.util.spec.SpecHelper
import org.specs2.mutable.Specification

final class PeopleApiSpec extends Specification with SpecHelper with QueryHelper {
  private val allPeopleQuery: String =
    s"""
       |{
       |  allPeople {
       |    $personFields
       |  }
       |}
    """.stripMargin

  private val personDetailsQuery: String =
    s"""
       |{
       |  personDetails(personId: "1") {
       |    $personFields
       |  }
       |}
    """.stripMargin

  "People" >> {
    "all people can be found" >> {
      val result = testQuery(allPeopleQuery)
      result must beAnInstanceOf[SuccessfulGraphQlResult]
    }

    "details for a specific person can be found" >> {
      val result = testQuery(personDetailsQuery)
      result must beAnInstanceOf[SuccessfulGraphQlResult]
    }
  }
}
