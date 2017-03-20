package com.redbubble.gql.product

import shapeless.tag
import shapeless.tag._

trait ConfigItemLabelTag

trait ConfigItemNameTag

trait ConfigItemValueTag

trait ConfigItemDisplayNameTag

trait ColourHexTag

trait MeasurementLabelTag

trait MeasurementChestTag

trait MeasurementLengthTag

package object config {
  type ConfigItemLabel = String @@ ConfigItemLabelTag
  type ConfigItemName = String @@ ConfigItemNameTag
  type ConfigItemValue = String @@ ConfigItemValueTag
  type ConfigItemDisplayName = String @@ ConfigItemDisplayNameTag
  type ColourHex = String @@ ColourHexTag
  type MeasurementLabel = String @@ MeasurementLabelTag
  type MeasurementChest = String @@ MeasurementChestTag
  type MeasurementLength = String @@ MeasurementLengthTag

  def ConfigItemLabel(t: String): @@[String, ConfigItemLabelTag] = tag[ConfigItemLabelTag](t)

  def ConfigItemName(t: String): @@[String, ConfigItemNameTag] = tag[ConfigItemNameTag](t)

  def ConfigItemValue(v: String): @@[String, ConfigItemValueTag] = tag[ConfigItemValueTag](v)

  def ConfigItemDisplayName(n: String): @@[String, ConfigItemDisplayNameTag] = tag[ConfigItemDisplayNameTag](n)

  def ColourHex(h: String): @@[String, ColourHexTag] = tag[ColourHexTag](h)

  def MeasurementLabel(l: String): @@[String, MeasurementLabelTag] = tag[MeasurementLabelTag](l)

  def MeasurementChest(c: String): @@[String, MeasurementChestTag] = tag[MeasurementChestTag](c)

  def MeasurementLength(l: String): @@[String, MeasurementLengthTag] = tag[MeasurementLengthTag](l)
}
