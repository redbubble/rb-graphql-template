package com.redbubble.gql.backends.icarus

import com.redbubble.gql.backends._
import com.redbubble.gql.backends.icarus.IcarusDecoders._
import com.redbubble.gql.product.Work
import com.redbubble.util.json.syntax._
import io.circe.Decoder

final case class IcarusCollection(works: Seq[Work], nextOffset: Offset)

object IcarusCollection {
  val icarusCollectionDecoder: Decoder[IcarusCollection] = Decoder.instance { c =>
    for {
      works <- c.downField("data").as[Seq[Work]](workDecoder.seqDecoder)
      newOffset <- c.downField("new_offset").as[Int].map(Offset)
    } yield IcarusCollection(works, newOffset)
  }
}
