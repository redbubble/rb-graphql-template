package com.redbubble.gql.graphql.schema.resolvers

import com.redbubble.graphql.GraphQlError.graphQlError
import com.redbubble.graphql.syntax._
import com.redbubble.gql.graphql.RootContext
import com.redbubble.gql.graphql.schema.inputKvPairsToTuples
import com.redbubble.gql.graphql.schema.types.InputTypes.{FieldSelectedConfig, FieldTypeId, SelectedProductConfigArg}
import com.redbubble.gql.graphql.schema.types.LocaleTypes._
import com.redbubble.gql.graphql.schema.types.WorkTypes._
import com.redbubble.gql.product.{Product, SelectedProductConfig}
import com.redbubble.gql.services.locale.Locale.localeForCodes
import com.redbubble.gql.services.product._
import com.redbubble.util.async.syntax._
import com.twitter.util.Future
import sangria.schema.{Action, Context}

object ProductDetails {
  private val SelectedParamName = SelectedProductConfigArg.name

  def productDetails(ctx: Context[RootContext, Unit]): Action[RootContext, Option[Product]] = {
    val details = for {
      typeId <- ctx.inputArgNamed(SelectedParamName, FieldTypeId).map(ProductTypeId)
      config <- ctx.inputArgNamed(SelectedParamName, FieldSelectedConfig)
    } yield {
      val workId = ctx.arg(WorkIdArg)
      val selectedConfig = SelectedProductConfig(typeId, inputKvPairsToTuples(config))
      val locale = localeForCodes(ctx.arg(CountryCodeArg), ctx.arg(CurrencyCodeArg), ctx.arg(LanguageCodeArg))
      ctx.ctx.productDetails(workId, selectedConfig, locale)
    }
    details.getOrElse(Future.exception(graphQlError(s"Unable to parse '${SelectedProductConfigArg.name}' fields"))).asScala
  }
}
