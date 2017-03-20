package com.redbubble.gql.graphql.schema.query

import com.redbubble.gql.graphql.schema.types.FeedTypes._
import com.redbubble.gql.graphql.schema.types.ImageTypes._
import com.redbubble.gql.graphql.schema.types.LocaleTypes._
import com.redbubble.gql.graphql.schema.types.PaginationTypes._
import com.redbubble.gql.graphql.schema.types.TopicTypes.TopicsArg
import com.redbubble.gql.graphql.{Pagination, RootContext}
import com.redbubble.gql.services.locale.Locale.localeForCodes
import com.redbubble.util.async.syntax._
import sangria.schema.{Field, ListType}

trait FeedApi {
  final val featuredCollectionFeedsField: Field[RootContext, Unit] = Field(
    name = "featuredCollectionFeeds",
    fieldType = ListType(FeedHeaderType),
    description = Some("A list of featured collection-based feeds."),
    resolve = ctx => ctx.ctx.featuredCollections().asScala
  )

  final val collectionFeedField: Field[RootContext, Unit] = Field(
    name = "collectionFeed",
    arguments = List(CollectionArg, CountryCodeArg, CurrencyCodeArg, LanguageCodeArg, LimitArg, OffsetArg, SizeClassArg),
    fieldType = CollectionFeedType,
    description = Some("A feed of works & products within a particular collection."),
    resolve = ctx => {
      val locale = localeForCodes(ctx.arg(CountryCodeArg), ctx.arg(CurrencyCodeArg), ctx.arg(LanguageCodeArg))
      val pagination = Pagination(ctx.arg(LimitArg), ctx.arg(OffsetArg))
      ctx.ctx.collectionFeed(ctx.arg(CollectionArg), locale, ctx.arg(SizeClassArg), pagination).asScala
    }
  )

  final val topicFeedField: Field[RootContext, Unit] = Field(
    name = "topicsFeed",
    arguments = List(TopicsArg, CountryCodeArg, CurrencyCodeArg, LanguageCodeArg, LimitArg, OffsetArg),
    fieldType = TopicsFeedType,
    description = Some("A feed of works & products for a selection of topics."),
    resolve = ctx => {
      val locale = localeForCodes(ctx.arg(CountryCodeArg), ctx.arg(CurrencyCodeArg), ctx.arg(LanguageCodeArg))
      val pagination = Pagination(ctx.arg(LimitArg), ctx.arg(OffsetArg))
      ctx.ctx.topicsFeed(ctx.arg(TopicsArg), locale, pagination).asScala
    }
  )

  final val featuredFeedsField: Field[RootContext, Unit] = featuredCollectionFeedsField.copy(
    name = "featuredFeeds",
    deprecationReason = Some("Use featuredCollectionFeeds instead.")
  )

  final val feedField: Field[RootContext, Unit] = collectionFeedField.copy(
    name = "feed",
    deprecationReason = Some("Use collectionFeed instead.")
  )
}
