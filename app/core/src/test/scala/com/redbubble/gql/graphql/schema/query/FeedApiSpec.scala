package com.redbubble.gql.graphql.schema.query

import com.redbubble.graphql.SuccessfulGraphQlResult
import com.redbubble.gql.graphql.schema.QueryHelper
import com.redbubble.gql.services.feed.Collection.validCollections
import com.redbubble.util.spec.SpecHelper
import org.specs2.mutable.Specification

final class FeedApiSpec extends Specification with SpecHelper with QueryHelper {

  val deprecatedFeaturedCollectionFeedQuery =
    s"""
       |query CollectionsFeedQuery {
       |  feed(country: "AU" currency: "AUD" limit: 1 offset: 0 collectionCode: "found") {
       |    header {
       |      $feedHeaderFields
       |    }
       |    $collectionFeedFields
       |    pagination {
       |      $paginationFields
       |    }
       |  }
       |}
    """.stripMargin

  val featuredCollectionFeedQuery =
    s"""
       |query CollectionsFeedQuery {
       |  collectionFeed(country: "AU" currency: "AUD" limit: 1 offset: 0 collectionCode: "found") {
       |    header {
       |      $feedHeaderFields
       |    }
       |    $collectionFeedFields
       |    pagination {
       |      $paginationFields
       |    }
       |  }
       |}
    """.stripMargin

  val deprecatedCollectionFeedQuery =
    s"""
       |query CollectionsFeedQuery {
       |  feed(country: "AU" currency: "AUD" limit: 1 offset: 0 collectionCode: "${validCollections.head.code}") {
       |    header {
       |      $feedHeaderFields
       |    }
       |    $collectionFeedFields
       |    pagination {
       |      $paginationFields
       |    }
       |  }
       |}
    """.stripMargin

  val collectionFeedQuery =
    s"""
       |query CollectionsFeedQuery {
       |  collectionFeed(country: "AU" currency: "AUD" limit: 1 offset: 0 collectionCode: "${validCollections.head.code}") {
       |    header {
       |      $feedHeaderFields
       |    }
       |    $collectionFeedFields
       |    pagination {
       |      $paginationFields
       |    }
       |  }
       |}
    """.stripMargin

  "The featured collection feed" >> {
    "can be queried using the deprecated field name" >> {
      val result = testQuery(deprecatedFeaturedCollectionFeedQuery)
      result must beAnInstanceOf[SuccessfulGraphQlResult]
    }
  }

  "The featured collection feed" >> {
    "can be queried" >> {
      val result = testQuery(featuredCollectionFeedQuery)
      result must beAnInstanceOf[SuccessfulGraphQlResult]
    }
  }

  "A collection feed" >> {
    "can be queried using the deprecated field name" >> {
      val result = testQuery(deprecatedCollectionFeedQuery)
      result must beAnInstanceOf[SuccessfulGraphQlResult]
    }
  }

  "A collection feed" >> {
    "can be queried" >> {
      val result = testQuery(collectionFeedQuery)
      result must beAnInstanceOf[SuccessfulGraphQlResult]
    }
  }

  val deprecatedFeaturedCollectionFeedsQuery =
    s"""
       |query FeaturedFeedsQuery {
       |	featuredFeeds {
       |    work {
       |      $workFields
       |    }
       |    image {
       |      $workImageFields
       |    }
       |    title
       |    collectionCode
       |  }
       |}
    """.stripMargin

  val featuredCollectionFeedsQuery =
    s"""
       |query FeaturedFeedsQuery {
       |	featuredCollectionFeeds {
       |    work {
       |      $workFields
       |    }
       |    image {
       |      $workImageFields
       |    }
       |    title
       |    collectionCode
       |  }
       |}
    """.stripMargin

  "The list of featured collection feeds" >> {
    "can be queried using the deprecated field name" >> {
      val result = testQuery(deprecatedFeaturedCollectionFeedsQuery)
      result must beAnInstanceOf[SuccessfulGraphQlResult]
    }
  }

  "The list of featured collection feeds" >> {
    "can be queried" >> {
      val result = testQuery(featuredCollectionFeedsQuery)
      result must beAnInstanceOf[SuccessfulGraphQlResult]
    }
  }

  val topicFeedQuery =
    s"""
       |query TopicsFeedQuery {
       |  topicsFeed(topics: ["dr who", "harry potter"], country: "AU", currency: "AUD", limit: 1, offset: 0) {
       |    $topicsFeedFields
       |    pagination {
       |      $paginationFields
       |    }
       |  }
       |}
    """.stripMargin

  "The featured collection feed" >> {
    "can be queried" >> {
      val result = testQuery(topicFeedQuery)
      result must beAnInstanceOf[SuccessfulGraphQlResult]
    }
  }
}
