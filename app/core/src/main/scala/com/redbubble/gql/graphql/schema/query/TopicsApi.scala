package com.redbubble.gql.graphql.schema.query

import com.redbubble.gql.graphql.schema.types.LocaleTypes.{CountryCodeArg, CurrencyCodeArg, LanguageCodeArg}
import com.redbubble.gql.graphql.schema.types.PaginationTypes._
import com.redbubble.gql.graphql.schema.types.TopicTypes._
import com.redbubble.gql.graphql.schema.types.WorkTypes._
import com.redbubble.gql.graphql.{Pagination, RootContext}
import com.redbubble.gql.services.locale.Locale.localeForCodes
import com.redbubble.util.async.syntax._
import sangria.schema.{Field, ListType}

trait TopicsApi {
  final val worksForTopicsField: Field[RootContext, Unit] = Field(
    name = "worksForTopics",
    arguments = List(TopicsArg, CountryCodeArg, CurrencyCodeArg, LanguageCodeArg, LimitArg, OffsetArg),
    fieldType = ListType(WorkType),
    description = Some("A list of works related to some topics."),
    resolve = ctx => {
      val locale = localeForCodes(ctx.arg(CountryCodeArg), ctx.arg(CurrencyCodeArg), ctx.arg(LanguageCodeArg))
      val pagination = Pagination(ctx.arg(LimitArg), ctx.arg(OffsetArg))
      ctx.ctx.worksForTopics(ctx.arg(TopicsArg), locale, pagination).asScala
    }
  )
}
