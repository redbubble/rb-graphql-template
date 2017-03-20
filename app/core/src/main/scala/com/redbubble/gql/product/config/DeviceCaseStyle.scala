package com.redbubble.gql.product.config

final case class DeviceCaseStyle(value: ConfigItemValue,
    valueDisplayName: ConfigItemDisplayName, defaultSelection: Boolean) extends ProductConfigurationItem {
  // Yes, these are inconsistent in the monolith API.
  override val name = ConfigItemName("type")
  override val label = ConfigItemLabel("Styles")
}
