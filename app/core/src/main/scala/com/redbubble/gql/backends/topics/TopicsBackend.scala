package com.redbubble.gql.backends.topics

import com.redbubble.gql.backends.MonolithLocale.monolithLocaleHeaders
import com.redbubble.gql.backends.Topic
import com.redbubble.gql.backends.works.WorksDecoders
import com.redbubble.gql.config.Environment.env
import com.redbubble.gql.graphql.Pagination
import com.redbubble.gql.product.Work
import com.redbubble.gql.services.locale.Locale
import com.redbubble.gql.util.http.GqlJsonApiClient.client
import com.redbubble.gql.util.metrics.Metrics._
import com.redbubble.util.http.RelativePath._
import com.redbubble.util.http.{DownstreamResponse, JsonApiClient, QueryParam, RelativePath}
import io.circe.Decoder

trait TopicsBackend {
  protected val topicsApiClient: JsonApiClient

  private val decodeWorks = WorksDecoders.workDataDecoder

  final def works(topics: Seq[Topic], locale: Locale, pagination: Pagination): DownstreamResponse[Seq[Work]] = {
    val paginationParams = Seq("limit" -> pagination.limit.toString, "offset" -> pagination.currentOffset.toString)
    val topicsParams = topics.map(t => "topics[]" -> t)
    val params = (topicsParams ++ paginationParams).map(QueryParam)
    topicsApiClient.get(RelativePath("works"), params, monolithLocaleHeaders(locale))(decodeWorks)
  }
}

object TopicsBackend extends TopicsBackend {
  protected override val topicsApiClient = client(env.topicsApiUrl)(clientMetrics)
}
