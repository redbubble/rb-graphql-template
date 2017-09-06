package com.redbubble.gql.graphql.schema

import com.redbubble.gql.graphql.GqlGraphQlQueryExecutor._
import com.redbubble.gql.util.async.globalAsyncExecutionContext
import com.redbubble.graphql.{GraphQlQuery, GraphQlResult}
import com.twitter.util.Await
import sangria.parser.QueryParser

import scala.util.Success

//noinspection TypeAnnotation
trait QueryHelper {

  val personFields =
    """
      |name
      |birthYear
      |hairColour
    """.stripMargin

  final def testQuery(rawQuery: String): GraphQlResult = {
    val Success(query) = QueryParser.parse(rawQuery)
    Await.result(executor.execute(GraphQlQuery(query, None, None))(globalAsyncExecutionContext))
  }
}
