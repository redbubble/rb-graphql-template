package com.redbubble.gql.util.http

import java.net.URL

import com.redbubble.gql.util.http.ClientOps.configureDefaults
import com.twitter.finagle.Http.Client

abstract class GqlHttpClient(baseUrl: URL) extends featherbed.Client(baseUrl)

object GqlHttpClient {
  /**
    * Default client configuration, including timeouts, retries, etc.
    */
  def defaultClient(baseUrl: URL): featherbed.Client =
    new GqlHttpClient(baseUrl) {
      override protected def clientTransform(client: Client) = configureDefaults(client)
    }
}
