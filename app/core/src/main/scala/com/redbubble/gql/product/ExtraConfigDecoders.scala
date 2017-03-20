package com.redbubble.gql.product

import com.redbubble.gql.product.config._
import com.redbubble.util.json.DecoderOps
import io.circe.Decoder._
import io.circe.{Decoder, DecodingFailure, HCursor}

trait ExtraConfigDecoders extends DecoderOps {
  def parseExtraConfigFields(cursor: HCursor): Seq[FlattenedConfigItem] = {
    val items = cursor.fields.map { fields =>
      fields.flatMap { name =>
        val nestedOptionsFields = parseOptionsField(cursor, name).getOrElse(Nil)
        val keyValueFields = parseKeyValueField(cursor, name).map(Seq(_)).getOrElse(Nil)
        nestedOptionsFields ++ keyValueFields
      }
    }
    items.getOrElse(Nil)
  }

  private val apparelStylesConfigItemDecoder: Decoder[FlattenedConfigItem] = Decoder.instance { c =>
    for {
      name <- c.up.up.downField("option_name").as[String].map(ConfigItemName)
      label <- c.up.up.downField("option_label").as[String].map(ConfigItemLabel)
      value <- c.downField("value").as[String].map(ConfigItemValue)
      displayName <- c.downField("display_name").as[String].map(ConfigItemDisplayName)
      selected <- c.downField("selected").as[Boolean]
    } yield FlattenedConfigItem(name, label, value, displayName, selected)
  }

  private val nestedOptionsConfigItemDecoder: Decoder[FlattenedConfigItem] = Decoder.instance { c =>
    for {
      name <- c.up.up.downField("option_name").as[String].map(ConfigItemName)
      label <- c.up.up.downField("option_label").as[String].map(ConfigItemLabel)
      value <- c.downField("value").as[String].map(ConfigItemValue)
      displayName <- c.downField("display_name").as[String].map(ConfigItemDisplayName)
      selected <- c.downField("selected").as[Boolean]
    } yield FlattenedConfigItem(name, label, value, displayName, selected)
  }

  private val nestedOptionsConfigItemSeqDecoder = decodeFocusSequence(nestedOptionsConfigItemDecoder.or(apparelStylesConfigItemDecoder))

  // Looks down inside options fields for config.
  private def parseOptionsField(cursor: HCursor, fieldName: String): Result[Seq[FlattenedConfigItem]] =
    cursor.downField(fieldName).downField("options").as[Seq[FlattenedConfigItem]](nestedOptionsConfigItemSeqDecoder)

  // Just look at this field for a key/value.
  private def parseKeyValueField(cursor: HCursor, fieldName: String): Result[FlattenedConfigItem] = {
    val value = cursor.downField(fieldName).focus.flatMap { json =>
      json.fold(
        None,
        jsonBoolean => Some(jsonBoolean.toString),
        jsonNumber => Some(jsonNumber.toString),
        jsonString => Some(jsonString),
        _ => None,
        _ => None
      )
    }
    value.map { v =>
      FlattenedConfigItem(ConfigItemName(fieldName),
        ConfigItemLabel(fieldName), ConfigItemValue(v), ConfigItemDisplayName(fieldName), defaultSelection = false)
    }.toRight(DecodingFailure("Unsupported value (null, array or object)", cursor.history))
  }
}
