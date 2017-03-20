package com.redbubble.gql.backends.icarus

import java.net.URL

import com.redbubble.gql.core._
import com.redbubble.gql.magickraum.{WorkImageDefinition, WorkImageRemoteKey}
import com.redbubble.gql.product.{Price, Work}
import com.redbubble.gql.services.artists._
import com.redbubble.gql.services.locale.Currency
import com.redbubble.gql.services.locale.Currency.currencyFromCodeDecoder
import com.redbubble.gql.services.product._
import com.redbubble.util.json.DecoderOps.urlDecoder
import io.circe.Decoder
import io.circe.Decoder._

trait IcarusDecoders {
  val artistDecoder: Decoder[Artist] = Decoder.instance { c =>
    for {
      id <- c.downField("artist_id").as[Option[Int]].map(_.map(i => Id(i.toString)))
      username <- c.downField("user_name").as[String].map(Username)
      name <- c.downField("artist_name").as[String].map(ArtistName)
      avatar <- c.downField("artist_avatar_url").as[URL](urlDecoder)
      location <- c.downField("location").as[Option[String]].map(_.map(Location))
    } yield Artist(id, username, name, avatar, location)
  }

  val priceDecoder: Decoder[Price] = Decoder.instance { c =>
    for {
      amount <- c.downField("price").as[BigDecimal]
      currency <- c.up.up.downField("currency").as[Currency](currencyFromCodeDecoder)
    } yield Price(amount, currency)
  }

  val workImageDefinitionDecoder: Decoder[WorkImageDefinition] = Decoder.instance { c =>
    for {
      workId <- c.downField("work_id").as[String].map(WorkId)
      remoteKey <- c.downField("work_original_image").downField("remote_key").as[Int].map(WorkImageRemoteKey)
      width <- c.downField("work_original_image").downField("dimensions").downField("width").as[Int].map(PixelWidth)
      height <- c.downField("work_original_image").downField("dimensions").downField("height").as[Int].map(PixelHeight)
    } yield WorkImageDefinition(remoteKey, workId, IrregularImageSize(width, height))
  }

  val workDecoder: Decoder[Work] = Decoder.instance { c =>
    for {
      workTitle <- c.downField("title").as[String].map(WorkTitle)
      workId <- c.downField("work_id").as[String].map(WorkId)
      workLink <- c.downField("work_link").as[URL](urlDecoder)
      workImage <- c.as[WorkImageDefinition](workImageDefinitionDecoder)
      artist <- c.as[Artist](artistDecoder)
    } yield Work(workId, workTitle, artist, workImage, workLink)
  }
}

object IcarusDecoders extends IcarusDecoders
