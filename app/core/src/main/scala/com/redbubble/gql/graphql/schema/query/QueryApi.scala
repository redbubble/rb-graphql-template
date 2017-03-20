package com.redbubble.gql.graphql.schema.query

import com.redbubble.gql.graphql.RootContext
import sangria.schema.{ObjectType, _}

object QueryApi extends FeedApi
    with WorksApi
    with TopicsApi
    with ProductsApi
    with ArtistsApi
    with LocaleApi
    with SearchApi
    with DeliveryApi
    with KnowledgeGraphApi {

  val QueryType: ObjectType[RootContext, Unit] = ObjectType(
    "QueryAPI",
    description = "The Redbubble iOS Query API.",
    fields = fields[RootContext, Unit](
      featuredCollectionFeedsField,
      collectionFeedField,
      featuredFeedsField,
      feedField,
      topicFeedField,
      workDetailsField,
      relatedWorksField,
      categoriesField,
      availableProductsField,
      productDetailsField,
      artistDetailsField,
      worksByArtistField,
      deviceLocaleField,
      allLocalesField,
      searchField,
      deliveryDatesField,
      relatedTopicsField,
      worksForTopicsField
    )
  )
}
