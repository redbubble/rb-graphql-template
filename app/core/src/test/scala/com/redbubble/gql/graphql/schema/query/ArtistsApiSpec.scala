package com.redbubble.gql.graphql.schema.query

import com.redbubble.graphql.SuccessfulGraphQlResult
import com.redbubble.gql.graphql.schema.QueryHelper
import com.redbubble.util.spec.SpecHelper
import org.specs2.mutable.Specification

final class ArtistsApiSpec extends Specification with SpecHelper with QueryHelper {
  val artistsWorksTopLevelQuery =
    s"""
       |{
       |  worksByArtist(username: "akwel", country:"AU", currency: "AUD") {
       |    $workFields
       |  }
       |}
    """.stripMargin

  val worksAllTheWayDownQuery =
    s"""
       |{
       |  worksByArtist(username: "akwel", country:"AU", currency: "AUD") {
       |    $workFields
       |  }
       |}
    """.stripMargin

  "Artists" >> {
    "can have their work found (at a top-level)" >> {
      val result = testQuery(artistsWorksTopLevelQuery)
      result must beAnInstanceOf[SuccessfulGraphQlResult]
    }

    "from anywhere an artist is represented" >> {
      val result = testQuery(worksAllTheWayDownQuery)
      result must beAnInstanceOf[SuccessfulGraphQlResult]
    }
  }
}
