package com.redbubble.gql.graphql.schema.query

import com.redbubble.gql.graphql.RootContext
import com.redbubble.gql.graphql.schema.types.LocaleTypes._
import com.redbubble.gql.graphql.schema.types.WorkTypes._
import com.redbubble.gql.services.locale.Locale.localeForCodes
import com.redbubble.util.async.syntax._
import sangria.schema.{Field, ListType, OptionType}

trait WorksApi {
  final val workDetailsField: Field[RootContext, Unit] = Field(
    name = "workDetails",
    arguments = List(WorkIdArg, CountryCodeArg, CurrencyCodeArg, LanguageCodeArg),
    fieldType = OptionType(WorkType),
    description = Some("Details of a work."),
    resolve = ctx => {
      val locale = localeForCodes(ctx.arg(CountryCodeArg), ctx.arg(CurrencyCodeArg), ctx.arg(LanguageCodeArg))
      ctx.ctx.peopleDetails(ctx.arg(WorkIdArg), locale).asScala
    }
  )

  final val relatedWorksField: Field[RootContext, Unit] = Field(
    name = "relatedWorks",
    arguments = List(WorkIdArg, CountryCodeArg, CurrencyCodeArg, LanguageCodeArg),
    fieldType = ListType(WorkType),
    description = Some("A list of related works."),
    resolve = ctx => {
      val locale = localeForCodes(ctx.arg(CountryCodeArg), ctx.arg(CurrencyCodeArg), ctx.arg(LanguageCodeArg))
      ctx.ctx.relatedWorks(ctx.arg(WorkIdArg), locale).asScala
    }
  )
}
