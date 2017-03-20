package com.redbubble.gql.graphql.schema.mutation

import com.redbubble.gql.graphql.RootContext
import com.redbubble.gql.graphql.schema.resolvers.AddToCart.{bulkAddToCart, bulkAddToCartWithLocale}
import com.redbubble.gql.graphql.schema.resolvers.DeviceRegistration.registerDevice
import com.redbubble.gql.graphql.schema.types.CartTypes._
import com.redbubble.gql.graphql.schema.types.DeviceTypes.{RegisteredDeviceType, _}
import com.redbubble.gql.graphql.schema.types.LocaleTypes._
import com.redbubble.gql.graphql.schema.types.SessionTypes._
import sangria.schema.{ObjectType, _}

object MutationApi {

  val MutationType: ObjectType[RootContext, Unit] = ObjectType(
    "MutationAPI",
    description = "The Redbubble iOS Mutation API.",
    fields[RootContext, Unit](
      Field(
        name = "registerDevice",
        arguments = List(RegisterDeviceArg),
        fieldType = OptionType(RegisteredDeviceType),
        resolve = registerDevice
      ),
      Field(
        name = "bulkAddToCart",
        arguments = List(BulkAddToCartArg),
        fieldType = OptionType(SessionType),
        resolve = bulkAddToCart,
        deprecationReason = Some("Use bulkAddToCartWithLocale. This field hard codes the locale to AU.")
      ),
      Field(
        name = "bulkAddToCartWithLocale",
        arguments = List(BulkAddToCartArg, CountryCodeArg, CurrencyCodeArg, LanguageCodeArg),
        fieldType = OptionType(SessionType),
        resolve = bulkAddToCartWithLocale
      )
    )
  )
}
