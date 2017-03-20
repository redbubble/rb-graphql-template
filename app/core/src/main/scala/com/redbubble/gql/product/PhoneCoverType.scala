package com.redbubble.gql.product

import com.redbubble.gql.product.config._

final case class PhoneCoverType(value: ConfigItemValue,
    valueDisplayName: ConfigItemDisplayName, defaultSelection: Boolean) extends ProductConfigurationItem {
  override val name = ConfigItemName("cover_type")
  override val label = ConfigItemLabel("Cover Type")
}

