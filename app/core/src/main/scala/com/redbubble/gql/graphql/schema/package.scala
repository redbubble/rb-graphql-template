package com.redbubble.gql.graphql

import com.redbubble.gql.backends._
import com.redbubble.gql.core._
import com.redbubble.gql.graphql.schema.types.InputTypes.{StringKvKeyType, StringKvValueType}
import sangria.marshalling.FromInput.InputObjectResult
import sangria.schema.InputObjectType._
import sangria.util.tag._

package object schema {
  type KvPairInputType = DefaultInput @@ InputObjectResult

  val DefaultImageSize: ImageSize = Size_768x768
  val DefaultImageWidth: PixelWidth = DefaultImageSize.width
  val DefaultImageHeight: PixelHeight = DefaultImageSize.height
  val DefaultImageSizeClass: SizeClass = SizeClass(DefaultImageWidth)
  val DefaultLimit: Limit = Limit(24)
  val DefaultOffset: Offset = Offset(0)
  val DefaultPage: Page = Page(1)

  def inputKvPairsToTuples(kvs: Seq[KvPairInputType]): Seq[(String, String)] =
    kvs.flatMap { m =>
      for {
        k <- m.get(StringKvKeyType.name)
        v <- m.get(StringKvValueType.name)
      } yield (k.toString, v.toString)
    }
}
