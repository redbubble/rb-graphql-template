package com.redbubble.gql.product.config

final case class GreetingCardSize(value: ConfigItemValue,
    valueDisplayName: ConfigItemDisplayName, defaultSelection: Boolean) extends ProductConfigurationItem {
  override val name = ConfigItemName("card_size")
  override val label = ConfigItemLabel("Size")
}
