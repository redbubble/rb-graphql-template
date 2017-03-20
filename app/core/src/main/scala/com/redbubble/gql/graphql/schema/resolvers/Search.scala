package com.redbubble.gql.graphql.schema.resolvers

import com.redbubble.gql.graphql.schema.types.LocaleTypes._
import com.redbubble.gql.graphql.schema.types.PaginationTypes._
import com.redbubble.gql.graphql.schema.types.ProductTypes._
import com.redbubble.gql.graphql.schema.types.SearchTypes._
import com.redbubble.gql.graphql.{Pagination, RootContext}
import com.redbubble.gql.services.locale.Locale.localeForCodes
import com.redbubble.gql.services.search.SearchResult
import com.redbubble.util.async.syntax._
import sangria.schema._

object Search {

  val SearchResultType = ObjectType(
    "SearchResult", "The result of a product search.",
    fields[Unit, SearchResult](
      Field(
        name = "products",
        fieldType = ListType(ProductType),
        description = Some("The products returned from a search."),
        resolve = _.value.products
      ),
      Field(
        "totalProducts",
        fieldType = IntType,
        description = Some("The total number of products found in this search."),
        resolve = _.value.totalProducts
      ),
      Field(
        name = "pagination",
        fieldType = PaginationType,
        description = Some("Details about the pagination options available."),
        resolve = _.value.pagination
      )
    )
  )

  def search(ctx: Context[RootContext, Unit]): Action[RootContext, SearchResult] = {
    val pagination = Pagination(ctx.arg(LimitArg), ctx.arg(OffsetArg))
    val locale = localeForCodes(ctx.arg(CountryCodeArg), ctx.arg(CurrencyCodeArg), ctx.arg(LanguageCodeArg))
    ctx.ctx.search(ctx.arg(SearchKeywordsArg), locale, pagination).asScala
  }
}
