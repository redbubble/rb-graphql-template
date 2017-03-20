package com.redbubble.gql.backends.artists

import com.redbubble.gql.backends.MonolithLocale._
import com.redbubble.gql.config.Environment.env
import com.redbubble.gql.product.Work
import com.redbubble.gql.services.artists.{Artist, _}
import com.redbubble.gql.services.locale.Locale
import com.redbubble.gql.util.http.GqlJsonApiClient.client
import com.redbubble.gql.util.metrics.Metrics._
import com.redbubble.util.http.RelativePath.emptyPath
import com.redbubble.util.http.{DownstreamResponse, JsonApiClient, QueryParam, RelativePath}

trait ArtistsBackend {
  protected val artistsApiClient: JsonApiClient

  private implicit val decodeArtist = ArtistDecoders.artistDataDecoder
  private implicit val decodeWorks = ArtistDecoders.artistWorksDataDecoder

  final def details(artists: Seq[Username], locale: Locale): DownstreamResponse[Seq[Artist]] = {
    val params = artists.map(u => "user_names[]" -> u).map(QueryParam)
    artistsApiClient.get[Seq[Artist]](emptyPath, params, monolithLocaleHeaders(locale))
  }

  final def works(artist: Username, locale: Locale): DownstreamResponse[Seq[Work]] =
    artistsApiClient.get[Seq[Work]](RelativePath(s"$artist/works"), Seq.empty, monolithLocaleHeaders(locale))
}

object ArtistsBackend extends ArtistsBackend {
  protected override val artistsApiClient = client(env.artistsApiUrl)(clientMetrics)
}

