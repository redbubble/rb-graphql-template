package com.redbubble.gql.backends.delivery

import com.redbubble.gql.config.Environment._
import com.redbubble.gql.services.delivery.{DeliveryDate, ProductName}
import com.redbubble.gql.services.locale.Country
import com.redbubble.gql.util.http.GqlJsonApiClient.client
import com.redbubble.gql.util.metrics.Metrics.clientMetrics
import com.redbubble.util.http.syntax._
import com.redbubble.util.http.{DownstreamResponse, JsonApiClient, QueryParam, RelativePath}

trait DeliveryDatesBackend {
  protected val deliveryDatesApiClient: JsonApiClient

  private implicit val decodeWorks = DeliveryDateDecoders.deliveryDateDecoder

  final def deliveryDates(productCode: ProductName, country: Country): DownstreamResponse[Option[DeliveryDate]] = {
    val params = Seq(
      "product_codes" -> productCode,
      "country_code" -> country.isoCode
    ).map(QueryParam)
    deliveryDatesApiClient.get[Option[DeliveryDate]](RelativePath(s"delivery_dates"), params).empty
  }
}

object DeliveryDatesBackend extends DeliveryDatesBackend {
  protected override val deliveryDatesApiClient = client(env.deliveryDatesApiUrl)(clientMetrics)
}
