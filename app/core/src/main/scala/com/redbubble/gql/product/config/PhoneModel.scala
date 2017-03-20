package com.redbubble.gql.product.config

final case class PhoneModel(value: ConfigItemValue,
    valueDisplayName: ConfigItemDisplayName, defaultSelection: Boolean) extends ProductConfigurationItem {
  override val name = ConfigItemName("phone_model")
  override val label = ConfigItemLabel("Model")
}
