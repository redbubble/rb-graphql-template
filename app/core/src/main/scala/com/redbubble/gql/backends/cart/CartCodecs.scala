package com.redbubble.gql.backends.cart

import java.net.URL

import com.redbubble.gql.auth.EmbeddedWebSession
import com.redbubble.gql.services.cart.CartProduct
import com.redbubble.util.json.DecoderOps
import io.circe.{Decoder, Encoder, Json}

trait CartCodecs {
  val cartProductEncoder: Encoder[CartProduct] = Encoder.instance { product =>
    val base = Json.obj(
      "work_id" -> Json.fromString(product.workId),
      "ia_code" -> Json.fromString(product.product.typeId),
      "quantity" -> Json.fromString(product.quantity.toString)
    )
    val selectedConfig = product.product.selectedConfig.map(tupleToJson)
    val cartConfig = product.product.cartConfig.map(tupleToJson)
    (selectedConfig ++ cartConfig).fold(base)((acc, j) => acc.deepMerge(j))
  }

  val cartProductsEncoder: Encoder[Seq[CartProduct]] = Encoder.instance { products =>
    Json.obj("products" -> Json.arr(products.map(cartProductEncoder.apply): _*))
  }

  val embeddedWebSessionDecoder: Decoder[EmbeddedWebSession] = Decoder.instance { c =>
    for {
      url <- c.downField("url").as[URL](DecoderOps.urlDecoder)
    } yield EmbeddedWebSession(url)
  }

  private def tupleToJson(t: (String, String)): Json = Json.obj(t._1 -> Json.fromString(t._2))
}

object CartCodecs extends CartCodecs
