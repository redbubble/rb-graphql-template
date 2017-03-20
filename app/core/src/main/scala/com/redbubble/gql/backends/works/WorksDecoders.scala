package com.redbubble.gql.backends.works

import java.net.URL

import com.redbubble.gql.backends.artists.ArtistDecoders.artistDecoder
import com.redbubble.gql.core._
import com.redbubble.gql.magickraum._
import com.redbubble.gql.product.Work
import com.redbubble.gql.services.artists.Artist
import com.redbubble.gql.services.product._
import com.redbubble.gql.util.log.CoreLogger
import com.redbubble.util.json.DecoderOps._
import com.redbubble.util.json.syntax._
import io.circe.Decoder

trait WorksDecoders extends CoreLogger {
  val workImageDefinitionDecoder: Decoder[WorkImageDefinition] = Decoder.instance { c =>
    for {
      workId <- c.downField("work_id").as[Int].map(i => WorkId(i.toString))
      remoteKey <- c.downField("work_original_image").downField("remote_key").as[Int].map(WorkImageRemoteKey)
      width <- c.downField("work_original_image").downField("dimensions").downField("width").as[Int].map(PixelWidth)
      height <- c.downField("work_original_image").downField("dimensions").downField("height").as[Int].map(PixelHeight)
    } yield WorkImageDefinition(remoteKey, workId, IrregularImageSize(width, height))
  }

  val workDecoder: Decoder[Work] = Decoder.instance { c =>
    for {
      workTitle <- c.downField("title").as[String].map(WorkTitle)
      workId <- c.downField("work_id").as[Int].map(i => WorkId(i.toString))
      workLink <- c.downField("work_link").as[URL](urlDecoder)
      workImage <- c.as[WorkImageDefinition](workImageDefinitionDecoder)
      artist <- c.downField("artist").as[Artist](artistDecoder)
    } yield Work(workId, workTitle, artist, workImage, workLink)
  }

  val workDataDecoder: Decoder[Seq[Work]] = Decoder.instance { c =>
    for {
      works <- c.downField("data").downField("works").as[Seq[Work]](workDecoder.seqDecoder)
    } yield works
  }
}

object WorksDecoders extends WorksDecoders
