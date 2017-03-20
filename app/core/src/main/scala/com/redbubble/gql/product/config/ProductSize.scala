package com.redbubble.gql.product.config

trait ProductSizeMeasurement {
  def label: MeasurementLabel
}

final case class ApparelSizeMeasurement(label: MeasurementLabel, chest: MeasurementChest, length: MeasurementLength)
    extends ProductSizeMeasurement

final case class ProductSize(value: ConfigItemValue, valueDisplayName: ConfigItemDisplayName,
    measurement: Option[ProductSizeMeasurement], defaultSelection: Boolean) extends ProductConfigurationItem {
  override val name = ConfigItemName("size")
  override val label = ConfigItemLabel("Size")
}
