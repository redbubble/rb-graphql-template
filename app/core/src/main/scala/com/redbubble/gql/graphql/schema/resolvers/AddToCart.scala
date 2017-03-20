package com.redbubble.gql.graphql.schema.resolvers

import com.redbubble.graphql.InputHelper
import com.redbubble.graphql.syntax._
import com.redbubble.gql.auth.EmbeddedWebSession
import com.redbubble.gql.graphql.RootContext
import com.redbubble.gql.graphql.schema._
import com.redbubble.gql.graphql.schema.types.CartTypes._
import com.redbubble.gql.graphql.schema.types.InputTypes._
import com.redbubble.gql.graphql.schema.types.LocaleTypes._
import com.redbubble.gql.product.SelectedProductConfig
import com.redbubble.gql.services.cart.{CartProduct, Quantity}
import com.redbubble.gql.services.locale.Locale
import com.redbubble.gql.services.locale.Locale._
import com.redbubble.gql.services.product._
import com.redbubble.util.async.syntax._
import mouse.string._
import sangria.marshalling.FromInput.InputObjectResult
import sangria.schema.InputObjectType.DefaultInput
import sangria.schema._
import sangria.util.tag.@@

object AddToCart extends InputHelper {

  def bulkAddToCart(ctx: Context[RootContext, Unit]): Action[RootContext, EmbeddedWebSession] = {
    // Default to Australia for old apps (that aren't using bulkAddToCartWithLocale).
    bulkAdd(ctx, AustraliaLocale)
  }

  def bulkAddToCartWithLocale(ctx: Context[RootContext, Unit]): Action[RootContext, EmbeddedWebSession] = {
    val locale = localeForCodes(ctx.arg(CountryCodeArg), ctx.arg(CurrencyCodeArg), ctx.arg(LanguageCodeArg))
    bulkAdd(ctx, locale)
  }

  private def parseProducts(productsInput: Option[Seq[@@[DefaultInput, InputObjectResult]]]): Seq[CartProduct] = {
    val productConfig = productsInput.map { productInputs =>
      val configs = productInputs.map { product =>
        for {
          workId <- product.get(FieldWorkId.name).map(w => WorkId(w.toString))
          typeId <- product.get(FieldTypeId.name).map(t => ProductTypeId(t.toString))
          quantity <- product.get(FieldQuantity.name).flatMap(q => q.toString.parseIntOption.map(Quantity))
          config <- product.get(FieldConfig.name).map(_.asInstanceOf[Seq[KvPairInputType]]).map(inputKvPairsToTuples)
        } yield {
          CartProduct(workId, SelectedProductConfig(typeId, config), quantity)
        }
      }
      configs.flatten
    }
    productConfig.getOrElse(Nil)
  }

  private def bulkAdd(ctx: Context[RootContext, Unit], locale: Locale) = {
    val productsInput = parseProducts(ctx.inputArg(FieldProducts))
    ctx.ctx.addToCart(productsInput, locale).asScala
  }
}
