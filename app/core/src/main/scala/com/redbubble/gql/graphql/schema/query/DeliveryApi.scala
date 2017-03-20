package com.redbubble.gql.graphql.schema.query

import com.redbubble.gql.graphql.RootContext
import com.redbubble.gql.graphql.schema.types.LocaleTypes.CountryCodeArg
import com.redbubble.gql.graphql.schema.types.ProductTypes._
import com.redbubble.util.async.syntax._
import sangria.schema.{Field, OptionType}

trait DeliveryApi {
  final val deliveryDatesField: Field[RootContext, Unit] = Field(
    name = "deliveryDates",
    arguments = List(ProductNameArg, CountryCodeArg),
    fieldType = OptionType(DeliveryDateType),
    description = Some("Estimated dates of delivery for a product."),
    resolve = ctx => {
      val productName = ctx.arg(ProductNameArg)
      val countryCode = ctx.arg(CountryCodeArg)
      ctx.ctx.estimatedDelivery(productName, countryCode).asScala
    }
  )
}
