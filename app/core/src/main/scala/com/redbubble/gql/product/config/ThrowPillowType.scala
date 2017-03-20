package com.redbubble.gql.product.config

final case class ThrowPillowType(value: ConfigItemValue,
    valueDisplayName: ConfigItemDisplayName, defaultSelection: Boolean) extends ProductConfigurationItem {
  override val name = ConfigItemName("type")
  override val label = ConfigItemLabel("Type")
}
