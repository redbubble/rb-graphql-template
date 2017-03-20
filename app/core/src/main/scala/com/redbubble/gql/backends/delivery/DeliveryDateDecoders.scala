package com.redbubble.gql.backends.delivery

import com.redbubble.gql.services.delivery.DeliveryDate
import io.circe.Decoder

trait DeliveryDateDecoders {
  val deliveryDateDecoder: Decoder[DeliveryDate] = Decoder.instance { c =>
    for {
      manufacturing <- c.downField("manufacturing").as[Int]
      lowStandard <- c.downField("low_standard_biz_days_incl_manufacturing").as[Int]
      highStandard <- c.downField("high_standard_biz_days_incl_manufacturing").as[Int]
      lowExpress <- c.downField("low_express_biz_days_incl_manufacturing").as[Int]
      highExpress <- c.downField("high_express_biz_days_incl_manufacturing").as[Int]
    } yield DeliveryDate(manufacturing, lowStandard, highStandard, lowExpress, highExpress)
  }
}

object DeliveryDateDecoders extends DeliveryDateDecoders
