package com.redbubble.gql.graphql.schema.query

import com.redbubble.gql.graphql.RootContext
import com.redbubble.gql.graphql.schema.resolvers.ProductDetails.productDetails
import com.redbubble.gql.graphql.schema.types.InputTypes.SelectedProductConfigArg
import com.redbubble.gql.graphql.schema.types.LocaleTypes._
import com.redbubble.gql.graphql.schema.types.ProductTypes._
import com.redbubble.gql.graphql.schema.types.WorkTypes._
import com.redbubble.gql.services.locale.Locale.localeForCodes
import com.redbubble.util.async.syntax._
import sangria.schema.{Field, ListType, OptionType}

trait ProductsApi {
  final val availableProductsField: Field[RootContext, Unit] = Field(
    name = "availableProducts",
    arguments = List(WorkIdArg, CountryCodeArg, CurrencyCodeArg, LanguageCodeArg),
    fieldType = ListType(ProductType),
    description = Some("A list of available products for a work (as supported by the iOS API)."),
    resolve = ctx => {
      val locale = localeForCodes(ctx.arg(CountryCodeArg), ctx.arg(CurrencyCodeArg), ctx.arg(LanguageCodeArg))
      ctx.ctx.availableProducts(ctx.arg(WorkIdArg), locale).asScala
    }
  )

  final val productDetailsField: Field[RootContext, Unit] = Field(
    name = "productDetails",
    arguments = List(WorkIdArg, CountryCodeArg, CurrencyCodeArg, LanguageCodeArg, SelectedProductConfigArg),
    fieldType = OptionType(ProductType),
    description = Some("The details of a product, given the inputs, such as currency, size, etc."),
    resolve = productDetails
  )

  final val categoriesField: Field[RootContext, Unit] = Field(
    name = "categories",
    fieldType = ListType(ProductCategoryType),
    description = Some("A list of top-level product categories."),
    resolve = ctx => ctx.ctx.topLevelCategories.map(_.toList).asScala
  )
}
