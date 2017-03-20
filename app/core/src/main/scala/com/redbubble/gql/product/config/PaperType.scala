package com.redbubble.gql.product.config

final case class PaperType(value: ConfigItemValue,
    valueDisplayName: ConfigItemDisplayName, defaultSelection: Boolean) extends ProductConfigurationItem {
  override val name = ConfigItemName("paper_type")
  override val label = ConfigItemLabel("Paper")
}
