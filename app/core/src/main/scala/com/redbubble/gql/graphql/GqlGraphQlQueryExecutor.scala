package com.redbubble.gql.graphql

import com.redbubble.graphql.GraphQlQueryExecutor
import com.redbubble.gql.graphql.schema.ApiSchema
import com.redbubble.gql.util.error.GqlErrorReporter.errorReporter
import com.redbubble.gql.util.metrics.Metrics.serverMetrics

object GqlGraphQlQueryExecutor {
  // Note. A depth of >= 9 is what is required for the introspection query to work.
  val executor: GraphQlQueryExecutor = GraphQlQueryExecutor.executor(
    ApiSchema.schema, RootContext, maxQueryDepth = 10)(errorReporter, serverMetrics)
}
