package com.redbubble.gql

import com.redbubble.gql.api.v1
import com.redbubble.gql.api.v1.GqlErrorHandler.apiErrorHandler
import com.redbubble.gql.api.v1.{GqlErrorHandler, ResponseEncoders}
import com.redbubble.gql.config.Config._
import com.redbubble.gql.config.{Development, Environment, Test}
import com.redbubble.gql.util.metrics.Metrics._
import com.redbubble.hawk.HawkAuthenticateRequestFilter
import com.redbubble.util.http.ExceptionFilter
import com.redbubble.util.http.filter.{HttpBasicAuthFilter, RequestLoggingFilter, RoutingMetricsFilter}
import com.twitter.finagle.Service
import com.twitter.finagle.http.{Request, Response}
import io.finch.Application

object ApiAuthFilter extends HawkAuthenticateRequestFilter(apiAuthenticationCredentials, whitelistedAuthPaths, serverMetrics)

object BasicAuthFilter extends HttpBasicAuthFilter(s"$systemName Security", basicAuthCredentials, basicAuthPaths, serverMetrics)

object UnhandledExceptionsFilter extends ExceptionFilter(ResponseEncoders.throwableEncode, GqlErrorHandler, serverMetrics)

object RouteMetricsFilter extends RoutingMetricsFilter(serverMetrics)

object GqlApi extends ResponseEncoders {
  private val api = v1.api

  def apiService: Service[Request, Response] = filters andThen api.handle(apiErrorHandler).toServiceAs[Application.Json]

  private def filters = {
    val baseFilters = RequestLoggingFilter andThen UnhandledExceptionsFilter andThen RouteMetricsFilter
    Environment.env match {
      case Development => baseFilters
      case Test => baseFilters
      case _ => baseFilters andThen ApiAuthFilter andThen BasicAuthFilter
    }
  }
}
