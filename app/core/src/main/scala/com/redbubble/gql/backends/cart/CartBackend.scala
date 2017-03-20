package com.redbubble.gql.backends.cart

import com.redbubble.gql.auth.EmbeddedWebSession
import com.redbubble.gql.backends.MonolithLocale._
import com.redbubble.gql.config.Environment.env
import com.redbubble.gql.services.cart.CartProduct
import com.redbubble.gql.services.locale.Locale
import com.redbubble.gql.util.http.GqlJsonApiClient.client
import com.redbubble.gql.util.metrics.Metrics._
import com.redbubble.util.http.{DownstreamResponse, JsonApiClient, RelativePath}

trait CartBackend {
  protected val cartApiClient: JsonApiClient

  private implicit val productsEncoder = CartCodecs.cartProductsEncoder
  private implicit val sessionDecoder = CartCodecs.embeddedWebSessionDecoder

  def bulkAdd(products: Seq[CartProduct], locale: Locale): DownstreamResponse[EmbeddedWebSession] = {
    val cartResponse = cartApiClient.postZip(RelativePath("bulk_add"), products, Seq.empty, monolithLocaleHeaders(locale))
    cartResponse.map { case (result, httpResponse) =>
      result.map { session =>
        val cookies = httpResponse.cookies.valuesIterator.toSeq
        session.copy(cookies = cookies)
      }
    }
  }
}

object CartBackend extends CartBackend {
  protected override val cartApiClient = client(env.cartApiUrl)(clientMetrics)
}

