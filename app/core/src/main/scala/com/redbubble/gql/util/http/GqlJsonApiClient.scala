package com.redbubble.gql.util.http

import java.net.URL

import com.redbubble.gql.config.Config
import com.redbubble.gql.util.async.futurePool
import com.redbubble.gql.util.http.GqlHttpClient.defaultClient
import com.redbubble.util.http.JsonApiClient
import com.redbubble.util.log.Logger
import com.redbubble.util.metrics.StatsReceiver

object GqlJsonApiClient {
  private val userAgent = "Redbubble GraphQL Template"
  private val logger = new Logger(s"${Config.coreLoggerName}.http")(futurePool)

  def client(baseUrl: URL)(implicit statsReceiver: StatsReceiver): JsonApiClient =
    JsonApiClient.client(defaultClient(baseUrl), userAgent, baseUrl.getHost)(statsReceiver, logger)

  def client(client: featherbed.Client, metricsId: String)(implicit statsReceiver: StatsReceiver): JsonApiClient = {
    JsonApiClient.client(client, userAgent, metricsId)(statsReceiver, logger)
  }
}
