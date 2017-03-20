package com.redbubble.gql.backends.locales

import java.net.InetAddress

import com.redbubble.gql.config.Environment.env
import com.redbubble.gql.services.locale.{Locale, LocaleInformation}
import com.redbubble.gql.util.http.GqlJsonApiClient.client
import com.redbubble.gql.util.metrics.Metrics._
import com.redbubble.util.http.{DownstreamResponse, JsonApiClient, QueryParam, RelativePath}

trait LocalesBackend {
  protected val localesApiClient: JsonApiClient

  private implicit val localeDecoder = LocalesDecoders.localeDecoder

  final def forIp(address: InetAddress): DownstreamResponse[Locale] = {
    val params = Seq("ip_address" -> address.getHostAddress).map(QueryParam)
    localesApiClient.get(RelativePath("for_ip"), params)
  }

  private implicit val localeInformationDecoder = LocalesDecoders.localeInformationDecoder

  final def allLocales(): DownstreamResponse[LocaleInformation] = localesApiClient.get(RelativePath("all"))
}

object LocalesBackend extends LocalesBackend {
  protected override val localesApiClient = client(env.localesApiUrl)(clientMetrics)
}
