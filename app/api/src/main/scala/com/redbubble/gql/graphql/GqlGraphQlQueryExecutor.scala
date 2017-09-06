package com.redbubble.gql.graphql

import com.redbubble.gql.graphql.schema.ApiSchema
import com.redbubble.gql.util.async.futurePool
import com.redbubble.gql.util.config.Config
import com.redbubble.gql.util.error.GqlErrorReporter.errorReporter
import com.redbubble.gql.util.metrics.Metrics.serverMetrics
import com.redbubble.graphql.GraphQlQueryExecutor
import com.redbubble.util.log.Logger

object GqlGraphQlQueryExecutor {
  private val logger = new Logger(s"${Config.coreLoggerName}.graphql")(futurePool)

  // Note. A depth of >= 9 is what is required for the introspection query to work.
  val executor: GraphQlQueryExecutor = GraphQlQueryExecutor.executor(
    ApiSchema.schema, RootContext, maxQueryDepth = 10)(errorReporter, serverMetrics, logger)
}
