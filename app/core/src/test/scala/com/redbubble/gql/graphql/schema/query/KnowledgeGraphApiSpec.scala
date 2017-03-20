package com.redbubble.gql.graphql.schema.query

import com.redbubble.graphql.SuccessfulGraphQlResult
import com.redbubble.gql.graphql.schema.QueryHelper
import com.redbubble.util.spec.SpecHelper
import org.specs2.mutable.Specification

final class KnowledgeGraphApiSpec extends Specification with SpecHelper with QueryHelper {
  val relatedTopicsQuery =
    s"""
       |{
       |  relatedTopics(topic: "drwho", language: "en") {
       |    baseTopic
       |    related {
       |      topic
       |      proximity
       |    }
       |    reformulations {
       |      topic
       |      reformulation
       |    }
       |  }
       |}
    """.stripMargin

  val emptyRelatedTopicsQuery =
    s"""
       |{
       |  relatedTopics(topic: " ") {
       |    baseTopic
       |    related {
       |      topic
       |      proximity
       |    }
       |    reformulations {
       |      topic
       |      reformulation
       |    }
       |  }
       |}
    """.stripMargin

  val defaultTopicsQuery =
    s"""
       |{
       |  relatedTopics(language: "en") {
       |    baseTopic
       |    related {
       |      topic
       |      proximity
       |    }
       |    reformulations {
       |      topic
       |      reformulation
       |    }
       |  }
       |}
    """.stripMargin

  "Topics" >> {
    "can have a default set returned" >> {
      val result = testQuery(defaultTopicsQuery)
      result must beAnInstanceOf[SuccessfulGraphQlResult]
    }

    "can have a default set returned when an empty string is passed" >> {
      val result = testQuery(emptyRelatedTopicsQuery)
      result must beAnInstanceOf[SuccessfulGraphQlResult]
    }

    "can have related topics found" >> {
      val result = testQuery(relatedTopicsQuery)
      result must beAnInstanceOf[SuccessfulGraphQlResult]
    }
  }
}
