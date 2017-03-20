package com.redbubble.gql.backends.icarus

import com.redbubble.gql.backends._
import com.redbubble.gql.config.Environment.env
import com.redbubble.gql.core._
import com.redbubble.gql.services.feed.{Collection, CollectionCode}
import com.redbubble.gql.services.locale.Locale
import com.redbubble.gql.util.http.GqlJsonApiClient.client
import com.redbubble.gql.util.metrics.Metrics._
import com.redbubble.util.http.{DownstreamResponse, JsonApiClient, QueryParam, RelativePath}

trait IcarusBackend {
  protected val icarusApiClient: JsonApiClient

  final def collectionFeed(collection: Collection,
      locale: Locale, limit: Limit, offset: Offset, imageSize: ImageSize): DownstreamResponse[IcarusCollection] = {
    val params = commonParams(locale, limit, offset, imageSize)
    requestCollection(collection.code, params)
  }

  private implicit val decoder = IcarusCollection.icarusCollectionDecoder

  private def requestCollection(code: CollectionCode, params: Seq[QueryParam]): DownstreamResponse[IcarusCollection] =
    icarusApiClient.get[IcarusCollection](RelativePath(s"collections/$code/feed"), params)

  private def commonParams(locale: Locale, limit: Limit, offset: Offset, imageSize: ImageSize): Seq[QueryParam] =
    Seq(
      "country" -> locale.country.isoCode,
      "currency" -> locale.currency.isoCode,
      "count" -> limit.toString,
      "offset" -> offset.toString,
      "image_dimension" -> imageSize.dimensions
    ).map(QueryParam)
}

object IcarusBackend extends IcarusBackend {
  protected override val icarusApiClient = client(env.collectionsApiUrl)(clientMetrics)
}
