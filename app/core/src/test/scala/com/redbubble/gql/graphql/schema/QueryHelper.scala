package com.redbubble.gql.graphql.schema

import com.redbubble.graphql.{GraphQlQuery, GraphQlResult}
import com.redbubble.gql.graphql.GqlGraphQlQueryExecutor._
import com.twitter.util.Await
import sangria.parser.QueryParser

import scala.util.Success

//noinspection TypeAnnotation
trait QueryHelper {

  import com.redbubble.gql.util.async.globalAsyncExecutionContext

  val workImageFields =
    """
      |url
      |size {
      |  height
      |  width
      |}
    """.stripMargin

  val productImageFields =
    """
      |url
      |size {
      |  height
      |  width
      |}
      |cropBox {
      |  origin {
      |    x
      |    y
      |  }
      |  width
      |  height
      |}
    """.stripMargin

  val paginationFields =
    """
      |currentOffset
      |nextOffset
      |moreResults
    """.stripMargin

  val artistFields =
    """
      |id
      |username
      |location
      |name
      |avatar(width: 800, height: 800)
    """.stripMargin

  val basicConfigurationFields =
    s"""
       |genericConfig {
       |  availableColours {
       |    name
       |    label
       |    value
       |    valueDisplayName
       |    defaultSelection
       |    hexValue
       |  }
       |  availableSizes {
       |    name
       |    label
       |    value
       |    valueDisplayName
       |    defaultSelection
       |    measurement {
       |      ... on ApparelSizeMeasurement {
       |        label
       |        chest
       |        length
       |      }
       |    }
       |  }
       |  addnConfig {
       |    name
       |    label
       |    value
       |    valueDisplayName
       |    defaultSelection
       |  }
       |}
     """.stripMargin

  val productConfigFields =
    s"""
       |config {
       |  __typename
       |  ... on ApparelConfig {
       |    sizes {
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
       |    colours {
       |      colour
       |      name
       |      label
       |      value
       |      valueDisplayName
       |      defaultSelection
       |    }
       |    addnConfig {
       |      name
       |      label
       |      value
       |      valueDisplayName
       |      defaultSelection
       |    }
       |  }
       |  ... on FramedPrintConfig {
       |    sizes {
       |      name
       |      label
       |      value
       |      valueDisplayName
       |      defaultSelection
       |    }
       |    frameStyles {
       |      name
       |      label
       |      value
       |      valueDisplayName
       |      defaultSelection
       |    }
       |    frameColours {
       |      name
       |      label
       |      value
       |      valueDisplayName
       |      defaultSelection
       |    }
       |    matteColours {
       |      name
       |      label
       |      value
       |      valueDisplayName
       |      defaultSelection
       |    }
       |    addnConfig {
       |      name
       |      label
       |      value
       |      valueDisplayName
       |      defaultSelection
       |    }
       |  }
       |  ... on PhoneCaseConfig {
       |    models {
       |      name
       |      label
       |      value
       |      valueDisplayName
       |      defaultSelection
       |    }
       |    coverTypes {
       |      name
       |      label
       |      value
       |      valueDisplayName
       |      defaultSelection
       |    }
       |    addnConfig {
       |      name
       |      label
       |      value
       |      valueDisplayName
       |      defaultSelection
       |    }
       |  }
       |  ... on JournalConfig {
       |    paperTypes {
       |      name
       |      label
       |      value
       |      valueDisplayName
       |      defaultSelection
       |    }
       |    addnConfig {
       |      name
       |      label
       |      value
       |      valueDisplayName
       |      defaultSelection
       |    }
       |  }
       |  ... on PrintConfig {
       |    sizes {
       |      name
       |      label
       |      value
       |      valueDisplayName
       |      defaultSelection
       |    }
       |    finishes {
       |      name
       |      label
       |      value
       |      valueDisplayName
       |      defaultSelection
       |    }
       |    addnConfig {
       |      name
       |      label
       |      value
       |      valueDisplayName
       |      defaultSelection
       |    }
       |  }
       |  ... on ArtPrintConfig {
       |    sizes {
       |      name
       |      label
       |      value
       |      valueDisplayName
       |      defaultSelection
       |    }
       |    addnConfig {
       |      name
       |      label
       |      value
       |      valueDisplayName
       |      defaultSelection
       |    }
       |  }
       |  ... on DeviceCaseConfig {
       |    styles {
       |      name
       |      label
       |      value
       |      valueDisplayName
       |      defaultSelection
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
     """.stripMargin

  val productCategoriesFields =
    s"""
       |categories {
       |  name
       |  products {
       |    typeId
       |    searchTerm
       |  }
       |}
     """.stripMargin

  val productFields =
    s"""
       |id
       |webLink
       |images {
       |  $productImageFields
       |}
       |typeId
       |typeName
       |workId
       |$basicConfigurationFields
       |$productConfigFields
       |$productCategoriesFields
    """.stripMargin

  val workFields =
    s"""
       |id
       |title
       |image(width: 800) {
       |  $workImageFields
       |}
       |webLink
       |artist {
       |  $artistFields
       |}
       |availableProducts(currency:"AUD", country:"AU") {
       |  $productFields
       |}
       |featuredProducts(currency:"AUD", country:"AU") {
       |  workId
       |  totalProducts
       |  featured {
       |    $productFields
       |  }
       |}
		""".stripMargin

  val feedHeaderFields =
    s"""
       |title
       |collectionCode
       |image {
       |  $productImageFields
       |}
       |work {
       |  $workFields
       |}
		 """.stripMargin

  val volumeHeaderFields =
    s"""
       |number
       |title
       |date
       |image {
       |  $productImageFields
       |}
       |work {
       |  $workFields
       |}
		 """.stripMargin

  val workFeedTileFragment =
    s"""
       |... on WorkFeedTile {
       |  __typename
       |  totalProducts
       |  work {
       |    $workFields
       |  }
       |}
		 """.stripMargin

  val productFeedTileFragment =
    s"""
       |... on ProductFeedTile {
       |  __typename
       |  product {
       |    $productFields
       |  }
       |}
		 """.stripMargin

  val similarWorksFeedTileFragment =
    s"""
       |... on SimilarWorksFeedTile {
       |  __typename
       |  baseWork {
       |    $workFields
       |  }
       |  similarWorks {
       |    $workFields
       |  }
       |}
		 """.stripMargin

  val artistFeedTileFragment =
    s"""
       |... on ArtistFeedTile {
       |  __typename
       |  artist {
       |    $artistFields
       |  }
       |}
		 """.stripMargin

  val featuredArtistFeedTileFragment =
    s"""
       |... on FeaturedArtistFeedTile {
       |  __typename
       |  featuredArtist {
       |    $artistFields
       |  }
       |}
		 """.stripMargin

  val textFeedTileFragment =
    s"""
       |... on TextFeedTile {
       |  __typename
       |  title
       |  caption
       |}
		 """.stripMargin

  val imageFeedTileFragment =
    s"""
       |... on ImageFeedTile {
       |  __typename
       |  url
       |  title
       |  caption
       |}
		 """.stripMargin

  val feedFields =
    s"""
       |slots {
       |  ... on StandardFeedSlot {
       |    tiles {
       |      $workFeedTileFragment
       |      $productFeedTileFragment
       |      $similarWorksFeedTileFragment
       |      $artistFeedTileFragment
       |      $featuredArtistFeedTileFragment
       |      $textFeedTileFragment
       |      $imageFeedTileFragment
       |    }
       |  }
       |}
		 """.stripMargin

  val collectionFeedFields =
    s"""
       |$feedFields
		 """.stripMargin

  val topicsFeedFields =
    s"""
       |topics
       |$feedFields
		 """.stripMargin

  val cookieFields =
    s"""
       |name
       |value
       |path
       |domain
       |comment
       |maxAge
       |version
       |httpOnly
       |isSecure
     """.stripMargin

  val sessionFields =
    s"""
       |webCheckoutUrl
       |  cookies {
       |    $cookieFields
       |}
     """.stripMargin

  val currencyFields =
    s"""
       |code
       |name
       |prefix
       |unit
     """.stripMargin

  val languageField =
    s"""
       |code
       |name
     """.stripMargin

  val countryField =
    s"""
       |code
       |name
     """.stripMargin

  def testQuery(rawQuery: String): GraphQlResult = {
    val Success(query) = QueryParser.parse(rawQuery)
    Await.result(executor.execute(GraphQlQuery(query, None, None))(globalAsyncExecutionContext))
  }
}
