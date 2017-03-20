package com.redbubble.gql.backends.knowledgegraph

import com.netaporter.uri.encoding.percentEncode
import com.redbubble.gql.backends.Topic
import com.redbubble.gql.config.Environment.env
import com.redbubble.gql.services.knowledgegraph.RelatedTopics
import com.redbubble.gql.util.http.ClientOps.connectionPoolAndTimeoutConfig
import com.redbubble.gql.util.http.GqlHttpClient
import com.redbubble.gql.util.http.GqlJsonApiClient.client
import com.redbubble.gql.util.metrics.Metrics._
import com.redbubble.util.http.filter.RetryFilter
import com.redbubble.util.http.filter.RetryFilter.RetryCondition
import com.redbubble.util.http.{DownstreamResponse, JsonApiClient, RelativePath}
import com.redbubble.util.io.Charset.DefaultCharset
import com.twitter.conversions.time._
import com.twitter.finagle.Http.Client
import com.twitter.finagle.Service
import com.twitter.finagle.http.Response
import com.twitter.finagle.http.Status.Accepted
import com.twitter.finagle.service.Backoff
import com.twitter.util.Return

trait KnowledgeGraphBackend {
  protected val knowledgeGraphApiClient: JsonApiClient

  final def topics(topic: Topic): DownstreamResponse[RelatedTopics] = {
    implicit val nodesDataDecoder = KnowledgeGraphDecoders.topicNodesDecoder(topic)
    val response = knowledgeGraphApiClient.get(RelativePath(s"topic/${percentEncode.encode(topic, DefaultCharset.name)}"))
    // Note. This endpoint doesn't always terminate and we eventually timeout, so we treat failures as empty results.
    response.map {
      case Left(_) => Right(RelatedTopics(topic, Seq(), Seq()))
      case r @ Right(_) => r
    }
  }
}

object KnowledgeGraphBackend extends KnowledgeGraphBackend {
  private val retries = Backoff.const(1.second).take(15)

  protected override val knowledgeGraphApiClient = {
    val c = new GqlHttpClient(env.knowledgeGraphApiUrl) {
      override protected def clientTransform(client: Client) = connectionPoolAndTimeoutConfig(client)

      override protected def serviceTransform[Req, Resp](service: Service[Req, Resp]) = RetryFilter.retry(service, retries, retryCondition)
    }
    client(c, env.knowledgeGraphApiUrl.getHost)(clientMetrics)
  }

  private def retryCondition[Req, Resp]: RetryCondition[Req, Resp] = {
    case (_, Return(rep: Response)) => rep.status == Accepted
  }
}
