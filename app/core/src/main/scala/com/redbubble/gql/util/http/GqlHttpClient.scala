package com.redbubble.gql.util.http

import java.net.URL

import com.redbubble.gql.util.http.ClientOps.configureDefaults
import com.redbubble.util.http.filter.HttpFilter
import com.twitter.finagle.Http.Client
import com.twitter.finagle.Service
import com.twitter.finagle.http.{Request, Response}

/**
  * A featherbed HTTP client.
  *
  * Contains sensible defaults for client connections including timeouts, etc. When you subclass this class, a client
  * can be customised in two ways:
  *
  * 1. Override `beforeFilters` and supply a set of `HttpFilter`s to be run before the base service is run, e.g.
  * `filter.andThen(client.newService(...))`;
  * 2. Override `configureClient` to get complete customisation of the client.
  *
  * @param baseUrl The base URL to the service this client will speak to. `com.redbubble.util.http.RelativePath`s are
  *                appended to this to contruct the URLs that will be used to the backend API.
  */
abstract class GqlHttpClient(baseUrl: URL) extends featherbed.Client(baseUrl) {
  def beforeFilters: Seq[HttpFilter] = Nil

  def configureClient(client: Client): Client = configureDefaults(client)

  final override protected def clientTransform(client: Client) = configureClient(client)



  final override protected def serviceTransform(service: Service[Request, Response]) =
    beforeFilters.foldLeft(service) { case (acc, filter) =>
      filter.andThen(acc)
    }
}

/**
  * The default HTTP client to use when speaking to backend APIs. Unless there is a specific reason to use a custom
  * client you should use this one.
  */
final class DefaultGqlHttpClient(baseUrl: URL) extends GqlHttpClient(baseUrl)

object GqlHttpClient {
  /**
    * Default client configuration, including timeouts, retries, etc.
    */
  def defaultClient(baseUrl: URL): featherbed.Client = new DefaultGqlHttpClient(baseUrl)
}
