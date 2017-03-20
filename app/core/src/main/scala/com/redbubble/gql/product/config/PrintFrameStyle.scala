package com.redbubble.gql.product.config

final case class PrintFrameStyle(value: ConfigItemValue,
    valueDisplayName: ConfigItemDisplayName, defaultSelection: Boolean) extends ProductConfigurationItem {
  override val name = ConfigItemName("frame_style")
  override val label = ConfigItemLabel("Frame Style")
}
