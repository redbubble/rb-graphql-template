package com.redbubble.gql.api.v1.graphql

import com.newrelic.api.agent.NewRelic
import com.redbubble.gql.api.v1.graphql.GraphQlRequestDecoders._
import com.redbubble.gql.graphql.GqlGraphQlQueryExecutor._
import com.redbubble.gql.util.async.globalAsyncExecutionContext
import com.redbubble.gql.util.cache.Cache._
import com.redbubble.gql.util.log.CoreLogger
import com.redbubble.gql.util.metrics.Metrics.serverMetrics
import com.redbubble.graphql.GraphQlEndpoint._
import com.redbubble.graphql._
import com.redbubble.util.cache._
import com.twitter.finagle.http.Status
import com.twitter.finagle.stats.Stat
import com.twitter.util.Future
import io.circe.Json
import io.finch._
import sangria.ast.OperationType.Query

object GraphQlApi extends CoreLogger {
  private val stats = serverMetrics.scope("graphql_operations")
  val graphQlApi = graphQlGet :+: graphQlPost

  def graphQlGet: Endpoint[Json] =
    get("graphql" :: graphqlQuery) { query: GraphQlQuery =>
      executeQuery(query)
    }

  def graphQlPost: Endpoint[Json] =
    post("graphql" :: jsonBody[GraphQlQuery]) { query: GraphQlQuery =>
      executeQuery(query)
    }

  //noinspection ScalaStyle
  private def executeQuery(query: GraphQlQuery): Future[Output[Json]] = {
    val operationName = query.operationName.getOrElse("unnamed_operation")
    NewRelic.setTransactionName(null, s"/graphql_operation/$operationName")
    NewRelic.addCustomParameter("operation", operationName)
    stats.counter("count", operationName).incr()
    Stat.timeFuture(stats.stat("execution_time", operationName)) {
      runQuery(query)
    }
  }

  private def runQuery(query: GraphQlQuery): Future[Output[Json]] = {
    def execute = executor.execute(query)(globalAsyncExecutionContext)

    // This bit is basically saying don't cache mutations.
    val result = query.document.operationType(None) match {
      case Some(Query) => graphQlQueryCache.caching(CacheKey(s"graphql.query:${query.hashCode().toString}"))(execute)
      case _ => execute
    }

    // Do our best to map the type of error back to a HTTP status code
    result.map {
      case SuccessfulGraphQlResult(json) => Output.payload(json, Status.Ok)
      case ClientErrorGraphQlResult(json, _) => Output.payload(json, Status.BadRequest)
      case BackendErrorGraphQlResult(json, _) => Output.payload(json, Status.InternalServerError)
    }
  }
}
