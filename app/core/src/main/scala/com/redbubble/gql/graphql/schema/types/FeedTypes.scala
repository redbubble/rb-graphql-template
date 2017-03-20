package com.redbubble.gql.graphql.schema.types

import com.redbubble.graphql.ScalarTypes.stringScalarType
import com.redbubble.gql.graphql.schema.types.ArtistTypes._
import com.redbubble.gql.graphql.schema.types.BaseTypes._
import com.redbubble.gql.graphql.schema.types.ImageTypes._
import com.redbubble.gql.graphql.schema.types.PaginationTypes._
import com.redbubble.gql.graphql.schema.types.ProductTypes._
import com.redbubble.gql.graphql.schema.types.TopicTypes.TopicType
import com.redbubble.gql.graphql.schema.types.WorkTypes._
import com.redbubble.gql.services.feed.{StandardFeedSlot, _}
import sangria.marshalling.ToInput.ScalarToInput
import sangria.schema.{Field, ListType, ObjectType, UnionType, _}
import sangria.validation.ValueCoercionViolation

object FeedTypes {

  //
  // Collection
  //

  private implicit val CollectionCodeInput = new ScalarToInput[CollectionCode]

  private case object CollectionCoercionViolation
      extends ValueCoercionViolation(s"Feed collection code expected, one of: ${Collection.validCollections.map(_.code).mkString(", ")}")

  private def parseCollectionCode(s: String): Either[CollectionCoercionViolation.type, Collection] =
    Collection.collectionFromCode(CollectionCode(s)).toRight(CollectionCoercionViolation)

  val CollectionCodeType = stringScalarType(
    "CollectionCode",
    s"A collection code, one of: ${Collection.validCollections.map(_.code).mkString(", ")}.",
    parseCollectionCode, () => CollectionCoercionViolation)

  val CollectionArg = Argument("collectionCode",
    CollectionCodeType,
    s"A collection code, one of: ${Collection.validCollections.map(_.code).mkString(", ")}.")

  val WorkFeedTileType = ObjectType(
    "WorkFeedTile", "A tile containing a work.",
    fields[Unit, WorkFeedTile](
      Field("work", WorkType, Some("A work."), resolve = _.value.work),
      Field("totalProducts", IntType, Some("The total number of available products for this work."), resolve = _.value.totalProducts)
    )
  )

  val ProductFeedTileType = ObjectType(
    "ProductFeedTile", "A tile containing a product.",
    fields[Unit, ProductFeedTile](
      Field("product", ProductType, Some("A product."), resolve = _.value.product)
    )
  )

  val SimilarWorksFeedTileType = ObjectType(
    "SimilarWorksFeedTile", "A tile containing works similar to another work.",
    fields[Unit, SimilarWorksFeedTile](
      Field("baseWork", WorkType, Some("A work (that other works are similar to)."), resolve = _.value.baseWork),
      Field("similarWorks", ListType(WorkType), Some("Similar works to `work`."), resolve = _.value.similarWorks)
    )
  )

  val ArtistFeedTileType = ObjectType(
    "ArtistFeedTile", "A tile containing an artist.",
    fields[Unit, ArtistFeedTile](
      Field("artist", ArtistType, Some("An artist."), resolve = _.value.artist)
    )
  )

  val FeaturedArtistFeedTileType = ObjectType(
    "FeaturedArtistFeedTile", "A tile containing an artist to be featured.",
    fields[Unit, FeaturedArtistFeedTile](
      Field("featuredArtist", ArtistType, Some("A featured artist."), resolve = _.value.featuredArtist)
    )
  )

  val TextFeedTileType = ObjectType(
    "TextFeedTile", "A tile containing text.",
    fields[Unit, TextFeedTile](
      Field("title", StringType, Some("Title text."), resolve = _.value.title),
      Field("caption", StringType, Some("Caption text."), resolve = _.value.caption)
    )
  )

  val ImageFeedTileType = ObjectType(
    "ImageFeedTile", "A tile containing an image and some text.",
    fields[Unit, ImageFeedTile](
      Field("url", StringType, Some("The url of the image."), resolve = _.value.imageUrl.toString),
      Field("title", StringType, Some("The title text."), resolve = _.value.title),
      Field("caption", StringType, Some("The image caption."), resolve = _.value.caption)
    )
  )

  private val tileTypes = List(
    WorkFeedTileType, ProductFeedTileType, SimilarWorksFeedTileType, ArtistFeedTileType, FeaturedArtistFeedTileType,
    TextFeedTileType, ImageFeedTileType
  )
  val TileType = UnionType[Unit](
    name = "Tile",
    description = Some(s"A tile within a slot; one of: ${unionTypeNames(tileTypes)}."),
    types = tileTypes
  )

  val StandardFeedSlot = ObjectType(
    "StandardFeedSlot", "A standard slot in a feed, containing tiles.",
    fields[Unit, StandardFeedSlot](
      Field("tiles",
        ListType(TileType),
        Some(s"Tiles in the slot; one of: ${unionTypeNames(TileType.types)}."),
        resolve = _.value.tiles)
    )
  )

  val FeedSlotType = UnionType[Unit]("FeedSlot",
    description = Some("A slot in a feed. Use `__typename` to discriminate on sub-types."),
    types = List(StandardFeedSlot)
  )

  val FeedHeaderType = ObjectType(
    "FeedHeader", "Metadata and hero image for the feed.",
    fields[Unit, CollectionFeedHeader](
      Field(
        name = "work",
        fieldType = OptionType(WorkType),
        description = Some(s"The work information for the hero image image."),
        resolve = _.value.work
      ),
      Field(
        name = "image",
        fieldType = OptionType(ImageType),
        description = Some(s"The hero image for this feed."),
        resolve = _.value.collection.headerImage
      ),
      Field(
        name = "title",
        fieldType = StringType,
        description = Some(s"The title of this feed."),
        resolve = _.value.collection.name
      ),
      Field(
        name = "collectionCode",
        fieldType = OptionType(StringType),
        description = Some(s"The identifier for this feed."),
        resolve = _.value.collection.code
      )
    )
  )

  val CollectionFeedType = ObjectType(
    "CollectionFeed", "A feed of products within a particular collection.",
    fields[Unit, CollectionFeed](
      Field(
        name = "header",
        fieldType = FeedHeaderType,
        description = Some(s"Metadata and hero image for the feed."),
        resolve = _.value.header
      ),
      Field(
        name = "slots",
        fieldType = ListType(FeedSlotType),
        description = Some(s"Slots in the feed, one of: ${unionTypeNames(FeedSlotType.types)}."),
        resolve = _.value.slots
      ),
      Field(
        name = "pagination",
        fieldType = PaginationType,
        description = Some("Details about the pagination options available."),
        resolve = _.value.pagination
      )
    )
  )

  val TopicsFeedType = ObjectType(
    "TopicsFeed", "A feed of works & products for a selection of topics.",
    fields[Unit, TopicsFeed](
      Field(
        name = "topics",
        fieldType = ListType(TopicType),
        description = Some(s"The topics that were used to generate the feed. This may be a different set of topics than that passed to `topicsFeed`."),
        resolve = _.value.topics
      ),
      Field(
        name = "slots",
        fieldType = ListType(FeedSlotType),
        description = Some(s"Slots in the feed, one of: ${unionTypeNames(FeedSlotType.types)}."),
        resolve = _.value.slots
      ),
      Field(
        name = "pagination",
        fieldType = PaginationType,
        description = Some("Details about the pagination options available."),
        resolve = _.value.pagination
      )
    )
  )
}
