package com.redbubble.perf.common

import io.gatling.core.Predef._
import io.gatling.core.body.{Body, StringBody}
import io.gatling.core.session.StaticStringExpression
import io.gatling.http.Predef._
import io.gatling.http.request.builder.HttpRequestBuilder

trait Graphql {
  final def graphqlRequest(requestName: String, payload: Body): HttpRequestBuilder =
    http(requestName).post("/graphql").body(payload).asJSON

  final def graphQlQueryBody(graphQlQuery: String): Body = StringBody(StaticStringExpression(jsonPayload(graphQlQuery)))

  private def jsonPayload(graphQlQuery: String): String =
    s"""
       |{
       |  "query": "${graphQlQuery.trim.replaceAll("\"","\\\\\"")}"
       |}
     """.stripMargin
}

object Graphql extends Graphql
