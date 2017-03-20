package com.redbubble.gql.backends.artists

import java.net.URL

import com.redbubble.gql.backends.works.WorksDecoders._
import com.redbubble.gql.core.Id
import com.redbubble.gql.product.Work
import com.redbubble.gql.services.artists._
import com.redbubble.util.json.DecoderOps._
import com.redbubble.util.json.syntax._
import io.circe.Decoder

trait ArtistDecoders {
  val artistDecoder: Decoder[Artist] = Decoder.instance { c =>
    for {
      id <- c.downField("id").as[Option[Int]].map(_.map(i => Id(i.toString)))
      username <- c.downField("username").as[String].map(Username)
      name <- c.downField("name").as[String].map(ArtistName)
      avatar <- c.downField("avatar").as[URL](urlDecoder)
      location <- c.downField("location").as[Option[String]].map(_.map(Location))
    } yield Artist(id, username, name, avatar, location)
  }

  val artistDataDecoder: Decoder[Seq[Artist]] = Decoder.instance { c =>
    for {
      works <- c.downField("data").as[Seq[Artist]](artistDecoder.seqDecoder)
    } yield works
  }

  val artistWorksDataDecoder: Decoder[Seq[Work]] = Decoder.instance { c =>
    for {
      works <- c.downField("data").as[Seq[Work]](workDecoder.seqDecoder)
    } yield works
  }
}

object ArtistDecoders extends ArtistDecoders
