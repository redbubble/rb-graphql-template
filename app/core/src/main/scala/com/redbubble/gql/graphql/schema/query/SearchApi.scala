package com.redbubble.gql.graphql.schema.query

import com.redbubble.gql.graphql.RootContext
import com.redbubble.gql.graphql.schema.resolvers.Search.{SearchResultType, search}
import com.redbubble.gql.graphql.schema.types.LocaleTypes._
import com.redbubble.gql.graphql.schema.types.PaginationTypes._
import com.redbubble.gql.graphql.schema.types.SearchTypes._
import sangria.schema.Field

trait SearchApi {
  val searchField: Field[RootContext, Unit] = Field(
    name = "search",
    arguments = List(SearchKeywordsArg, CountryCodeArg, CurrencyCodeArg, LanguageCodeArg, LimitArg, OffsetArg),
    fieldType = SearchResultType,
    description = Some("Keyword-based product search."),
    resolve = search
  )
}
