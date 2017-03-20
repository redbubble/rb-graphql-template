package com.redbubble.gql.product

import java.net.URL

import com.redbubble.gql.core.Image._
import com.redbubble.gql.core._
import com.redbubble.gql.product.ProductConfigDecoders._
import com.redbubble.gql.product.config._
import com.redbubble.gql.services.locale.Currency
import com.redbubble.gql.services.locale.Currency._
import com.redbubble.gql.services.product._
import com.redbubble.util.json.DecoderOps
import com.redbubble.util.json.syntax._
import io.circe.Decoder
import io.circe.Decoder._

trait ProductDecoders extends DecoderOps {
  final val productImagesDecoder: Decoder[Seq[Image]] = Decoder.instance { c =>
    c.as[Seq[URL]](urlSeqDecoder).map(urls => urls.map(imageFromUrl))
  }

  final val productDefinitionDecoder: Decoder[ProductDefinition] = Decoder.instance { c =>
    for {
      typeId <- c.downField("ia_code").as[String]
      typeName <- c.downField("display_name").as[String]
    } yield ProductDefinition(ProductTypeId(typeId), ProductDisplayName(typeName))
  }

  final val priceDecoder: Decoder[Price] = Decoder.instance { c =>
    for {
      currency <- c.downField("price").downField("currency").as[Currency](currencyFromCodeDecoder)
      amount <- c.downField("price").downField("amount").as[BigDecimal]
    } yield Price(amount, currency)
  }

  final val productDecoder: Decoder[Product] = Decoder.instance { c =>
    for {
      productId <- c.downField("id").as[String].map(ProductId)
      workId <- c.downField("work_id").as[Int].map(i => WorkId(i.toString))
      images <- c.downField("product_images").as[Seq[Image]](productImagesDecoder)
      info <- c.downField("product_info").as[Seq[String]].map(ProductInfo)
      webLink <- c.downField("product_page").as[URL](urlDecoder)
      price <- c.as[Price](priceDecoder)
      definition <- c.as[ProductDefinition](productDefinitionDecoder)
      config <- c.downField("options").as[Option[ProductConfiguration]](decodeOption(productConfigurationDecoder(definition)))
    } yield Product(productId, workId, definition, info, config, price, webLink, images)
  }

  final val productsDataDecoder: Decoder[Seq[Product]] = Decoder.instance { c =>
    c.downField("data").downField("products").as[Seq[Product]](productDecoder.seqDecoder)
  }

  final val productDataDecoder: Decoder[Product] = Decoder.instance { c =>
    c.downField("data").downField("product").as[Product](productDecoder)
  }
}

object ProductDecoders extends ProductDecoders
