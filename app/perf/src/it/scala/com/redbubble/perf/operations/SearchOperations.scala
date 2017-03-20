
package com.redbubble.perf.operations

import com.redbubble.perf.common.Graphql._
import com.redbubble.perf.queries.SearchQueries._
import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder

trait SearchOperations {
  final def searchFor(keywords: String, offset: Int = 0): ChainBuilder = {
    exec(
      graphqlRequest("Search", graphQlQueryBody(searchQuery(keywords, offset)))
    )
  }
}
