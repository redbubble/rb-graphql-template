package com.redbubble.gql.backends.search

import com.redbubble.gql.backends.MonolithLocale.monolithLocaleHeaders
import com.redbubble.gql.backends._
import com.redbubble.gql.config.Environment.env
import com.redbubble.gql.services.locale.Locale
import com.redbubble.gql.services.search._
import com.redbubble.gql.util.http.GqlJsonApiClient.client
import com.redbubble.gql.util.metrics.Metrics._
import com.redbubble.util.http.{DownstreamResponse, JsonApiClient, QueryParam, RelativePath}

trait SearchBackend {
  protected val searchApiClient: JsonApiClient

  private implicit val resultsDecoder = SearchResultDecoders.rawSearchResultDataDecoder

  final def search(keywords: SearchKeywords,
      locale: Locale, page: Page, perPage: ResultsPerPage): DownstreamResponse[RawSearchResult] = {
    val params = Seq(
      "query" -> keywords,
      "page" -> page.toString,
      "limit" -> perPage.toString
    ).map(QueryParam)
    searchApiClient.get(RelativePath("find"), params, monolithLocaleHeaders(locale))
  }
}

object SearchBackend extends SearchBackend {
  protected override val searchApiClient = client(env.searchApiUrl)(clientMetrics)
}
