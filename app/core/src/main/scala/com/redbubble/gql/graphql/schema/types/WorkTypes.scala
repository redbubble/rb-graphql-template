package com.redbubble.gql.graphql.schema.types

import com.redbubble.graphql.ScalarTypes.stringScalarType
import com.redbubble.gql.graphql.RootContext
import com.redbubble.gql.graphql.schema.types.ArtistTypes._
import com.redbubble.gql.graphql.schema.types.BaseTypes._
import com.redbubble.gql.graphql.schema.types.ImageTypes._
import com.redbubble.gql.graphql.schema.types.LocaleTypes._
import com.redbubble.gql.graphql.schema.types.ProductTypes._
import com.redbubble.gql.magickraum.ImageGenerator
import com.redbubble.gql.product.Work
import com.redbubble.gql.services.locale.Locale._
import com.redbubble.gql.services.product._
import com.redbubble.util.async.syntax._
import sangria.marshalling.ToInput.ScalarToInput
import sangria.schema._
import sangria.validation.ValueCoercionViolation

object WorkTypes {
  //
  // Work Id
  //

  private implicit val workIdInput = new ScalarToInput[WorkId]

  private case object WorkIdCoercionViolation
      extends ValueCoercionViolation(s"Work Id expected.")

  private def parseWorkId(s: String): Either[WorkIdCoercionViolation.type, WorkId] = Right(WorkId(s))

  val WorkIdType = stringScalarType(
    "WorkId",
    s"The Id of a work.",
    parseWorkId, () => WorkIdCoercionViolation
  )

  val WorkIdArg = Argument("workId",
    WorkIdType,
    s"The Id of a work."
  )

  val WorkType = ObjectType(
    "Work", "An artwork created by an artist.",
    interfaces[RootContext, Work](identifiableType[RootContext]),
    () => fields[RootContext, Work](
      Field("title", StringType, Some("The title of work."), resolve = _.value.title),
      Field(
        name = "image",
        fieldType = WorkImageType,
        description = Some("The work's image."),
        arguments = List(WidthArg),
        resolve = ctx => ImageGenerator.generateWorkImageForWidth(ctx.value.imageDefinition, ctx.arg(WidthArg))
      ),
      Field(
        name = "webLink",
        fieldType = StringType,
        description = Some("The URL of the work on the Redbubble website."),
        resolve = _.value.webLink.toString
      ),
      Field("artist", ArtistType, Some("The artist who produced this work."), resolve = _.value.artist),
      Field(
        name = "featuredProducts",
        arguments = List(CountryCodeArg, CurrencyCodeArg, LanguageCodeArg),
        fieldType = FeaturedProductsType,
        description = Some("A short list of featured products for this work."),
        resolve = ctx => ctx.ctx.featuredProducts(ctx.value.workId, localeForCodes(ctx.arg(CountryCodeArg), ctx.arg(CurrencyCodeArg), ctx.arg(LanguageCodeArg))).asScala
      ),
      Field(
        name = "availableProducts",
        arguments = List(CountryCodeArg, CurrencyCodeArg, LanguageCodeArg),
        fieldType = ListType(ProductType),
        description = Some("A list of available products for a work (as supported by the iOS API)."),
        resolve = ctx => ctx.ctx.availableProducts(ctx.value.workId, localeForCodes(ctx.arg(CountryCodeArg), ctx.arg(CurrencyCodeArg), ctx.arg(LanguageCodeArg))).asScala
      )
    )
  )
}
