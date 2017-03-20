package com.redbubble.perf.operations

import com.redbubble.gql.services.feed.CollectionCode
import com.redbubble.perf.common.Graphql._
import com.redbubble.perf.queries.FeedQueries._
import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder

trait FeedOperations {
  final val faturedFeeds = {
    exec(
      graphqlRequest("Featured Feeds", graphQlQueryBody(featuredFeedsQuery))
    )
  }

  final def feed(code: CollectionCode, offset: Int = 0): ChainBuilder = {
    exec(
      graphqlRequest(s"Feed '$code'", graphQlQueryBody(feedQuery(code, offset)))
    )
  }
}
