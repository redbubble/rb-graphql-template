package com.redbubble.gql.product.config

final case class PrintFinish(value: ConfigItemValue,
    valueDisplayName: ConfigItemDisplayName, defaultSelection: Boolean) extends ProductConfigurationItem {
  override val name = ConfigItemName("finish")
  override val label = ConfigItemLabel("Finish")
}
