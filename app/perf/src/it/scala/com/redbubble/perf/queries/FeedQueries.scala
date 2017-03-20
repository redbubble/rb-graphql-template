package com.redbubble.perf.queries

import com.redbubble.gql.services.feed._

//noinspection ScalaStyle
trait FeedQueries {
  val foundFeedCode = CollectionCode("found")

  val featuredFeedsQuery =
    s"""
       |{
       |  featuredFeeds {
       |    image {
       |      url
       |      size {
       |        width
       |        height
       |      }
       |    }
       |    title
       |    collectionCode
       |  }
       |}
     """.stripMargin

  def feedQuery(collectionCode: CollectionCode, offset: Int = 0): String =
    s"""
       |{
       |  feed(collectionCode: "found", currency: "AUD", country: "AU", offset: $offset) {
       |    slots {
       |      ...slotTypes
       |    }
       |    pagination {
       |      currentOffset
       |      nextOffset
       |      moreResults
       |    }
       |  }
       |}
       |
       |fragment slotTypes on FeedSlot {
       |  __typename
       |  ... on StandardFeedSlot {
       |    tiles {
       |      ... on WorkFeedTile {
       |        __typename
       |        work {
       |          ...workDetails
       |        }
       |      }
       |      ... on ProductFeedTile {
       |        __typename
       |        product {
       |          ...productDetails
       |        }
       |      }
       |      ... on SimilarWorksFeedTile {
       |        ...similarWorksTile
       |      }
       |      ... on ArtistFeedTile {
       |        __typename
       |        artist {
       |          ...artistDetails
       |        }
       |      }
       |      ... on FeaturedArtistFeedTile {
       |        __typename
       |        featuredArtist {
       |          ...artistDetails
       |        }
       |      }
       |      ... on TextFeedTile {
       |        __typename
       |        title
       |        caption
       |      }
       |      ... on ImageFeedTile {
       |        __typename
       |        url
       |        title
       |        caption
       |      }
       |    }
       |  }
       |}
       |
       |fragment similarWorksTile on SimilarWorksFeedTile {
       |  __typename
       |  baseWork {
       |    ...workDetails
       |  }
       |  similarWorks {
       |    ...workDetails
       |  }
       |}
       |
       |fragment productDetails on Product {
       |  id
       |  price {
       |    ...priceDetails
       |  }
       |  images {
       |    ...imageDetails
       |  }
       |  typeId
       |  typeName
       |  webLink
       |  categories {
       |    name
       |  }
       |  genericConfig {
       |    availableColours {
       |      name
       |      label
       |      value
       |      valueDisplayName
       |      defaultSelection
       |      hexValue
       |    }
       |    availableSizes {
       |      name
       |      label
       |      value
       |      valueDisplayName
       |      defaultSelection
       |      measurement {
       |        ... on ApparelSizeMeasurement {
       |          label
       |          chest
       |          length
       |        }
       |      }
       |    }
       |    addnConfig {
       |      name
       |      label
       |      value
       |      valueDisplayName
       |      defaultSelection
       |    }
       |  }
       |}
       |
       |fragment imageDetails on Image {
       |  url
       |  size {
       |    width
       |    height
       |  }
       |}
       |
       |fragment workImageDetails on WorkImage {
       |  url
       |  size {
       |    width
       |    height
       |  }
       |}
       |
       |fragment workDetails on Work {
       |  id
       |  title
       |  image(width: 1024) {
       |    ...workImageDetails
       |  }
       |  webLink
       |  artist {
       |    ...artistDetails
       |  }
       |}
       |
       |fragment artistDetails on Artist {
       |  name
       |  avatar
       |  id
       |}
       |
       |fragment priceDetails on Price {
       |  amount
       |  currency
       |  prefix
       |  unit
       |}
     """.stripMargin
}

object FeedQueries extends FeedQueries
