package com.redbubble.gql.graphql.schema.query

import com.redbubble.gql.graphql.RootContext
import com.redbubble.gql.graphql.schema.types.LocaleTypes._
import com.redbubble.util.async.syntax._
import sangria.schema.Field

trait LocaleApi {
  final val deviceLocaleField: Field[RootContext, Unit] = Field(
    name = "deviceLocale",
    arguments = List(InetAddressArg),
    fieldType = LocaleType,
    description = Some("Returns locale information (country, language, currency) for a device based on its IP address."),
    resolve = ctx => ctx.ctx.deviceLocale(ctx.arg(InetAddressArg)).asScala
  )

  final val allLocalesField: Field[RootContext, Unit] = Field(
    name = "allLocales",
    fieldType = LocaleInformationType,
    description = Some("All supported countries, currencies & langauges."),
    resolve = ctx => ctx.ctx.allLocales().asScala
  )
}
