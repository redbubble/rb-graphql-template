package com.redbubble.gql.graphql.schema.query

import com.redbubble.gql.graphql.RootContext
import com.redbubble.gql.graphql.schema.types.ArtistTypes._
import com.redbubble.gql.graphql.schema.types.LocaleTypes._
import com.redbubble.gql.graphql.schema.types.WorkTypes._
import com.redbubble.gql.services.locale.Locale.localeForCodes
import com.redbubble.util.async.syntax._
import sangria.schema.{Field, ListType, OptionType}

trait ArtistsApi {
  final val worksByArtistField: Field[RootContext, Unit] = Field(
    name = "worksByArtist",
    arguments = List(UsernameArg, CountryCodeArg, CurrencyCodeArg, LanguageCodeArg),
    fieldType = ListType(WorkType),
    description = Some("The works created by an artist."),
    resolve = ctx => {
      val locale = localeForCodes(ctx.arg(CountryCodeArg), ctx.arg(CurrencyCodeArg), ctx.arg(LanguageCodeArg))
      ctx.ctx.worksByArtist(ctx.arg(UsernameArg), locale).asScala
    }
  )

  final val artistDetailsField: Field[RootContext, Unit] = Field(
    name = "artistDetails",
    arguments = List(UsernameArg, CountryCodeArg, CurrencyCodeArg, LanguageCodeArg),
    fieldType = OptionType(ArtistType),
    description = Some("The details of an artist."),
    resolve = ctx => {
      val locale = localeForCodes(ctx.arg(CountryCodeArg), ctx.arg(CurrencyCodeArg), ctx.arg(LanguageCodeArg))
      ctx.ctx.artistDetails(ctx.arg(UsernameArg), locale).asScala
    }
  )
}
