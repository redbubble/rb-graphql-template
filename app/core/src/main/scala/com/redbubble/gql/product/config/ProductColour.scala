package com.redbubble.gql.product.config

/**
  * The colour of a product.
  */
trait ProductColour extends ProductConfigurationItem {
  def hexValue: Option[ColourHex]
}

/**
  * The colour of an apparel product.
  */
final case class ApparelColour(value: ConfigItemValue, valueDisplayName: ConfigItemDisplayName,
    colour: ColourHex, defaultSelection: Boolean) extends ProductColour {
  override val name = ConfigItemName("body_color")
  override val label = ConfigItemLabel("Color")
  override val hexValue = Some(colour)
}

/**
  * The colour of a frame.
  */
final case class FrameColour(value: ConfigItemValue,
    valueDisplayName: ConfigItemDisplayName, defaultSelection: Boolean) extends ProductColour {
  override val name = ConfigItemName("frame_color")
  override val label = ConfigItemLabel("Frame Color")
  override val hexValue = None
}

/**
  * The colour of the background a print is framed with.
  */
final case class MatteColour(value: ConfigItemValue,
    valueDisplayName: ConfigItemDisplayName, defaultSelection: Boolean) extends ProductColour {
  override val name = ConfigItemName("matte_color")
  override val label = ConfigItemLabel("Matte Color")
  override val hexValue = None
}

/**
  * The colour of a clock hand.
  */
final case class HandColour(value: ConfigItemValue,
    valueDisplayName: ConfigItemDisplayName, defaultSelection: Boolean) extends ProductColour {
  override val name = ConfigItemName("hand_color")
  override val label = ConfigItemLabel("Hand Color")
  override val hexValue = None
}
