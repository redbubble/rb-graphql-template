package com.redbubble.gql.product.config

final case class MugStyle(value: ConfigItemValue,
    valueDisplayName: ConfigItemDisplayName, defaultSelection: Boolean) extends ProductConfigurationItem {
  override val name = ConfigItemName("style")
  override val label = ConfigItemLabel("Styles")
}
