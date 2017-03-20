package com.redbubble.gql.backends.product

import com.redbubble.gql.core.Image
import com.redbubble.gql.product.config.{ApparelColour, _}
import com.redbubble.gql.product.{PhoneCoverType, Product}

trait ProductApiJson {
  def samsungGalaxyCaseProductJson(product: Product): String = {
    val config = product.config.get.asInstanceOf[PhoneCaseConfig]
    s"""
       |{
       |  "data": {
       |    "products": [
       |      {
       |        "id": "${product.id}",
       |        "work_id": ${product.workId},
       |        "product_info": [
       |          ${product.info.map(productInfoJson).mkString(",")}
       |        ],
       |        "ia_code": "${product.definition.typeId}",
       |        "display_name": "${product.definition.displayName}",
       |        "product_image": ${productImageJson(product.images.head)},
       |        "product_images": [
       |           ${product.images.map(productImageJson).mkString(",")}
       |         ],
       |        "product_page": "${product.webLink.toString}",
       |        "price": {
       |           "currency": "${product.price.currency.isoCode}",
       |           "amount": "${product.price.amount.toString}"
       |        },
       |        ${samsungGalaxyCaseOptionsJson(config.models, config.coverTypes)}
       |      }
       |    ]
       |  }
       |}
    """.stripMargin
  }

  def productInfoJson(s: String): String = s""" "${s}" """.trim

  def productImageJson(image: Image): String = s""" "${image.url.toString}" """.trim

  def samsungGalaxyCaseOptionsJson(models: Seq[PhoneModel], coverTypes: Seq[PhoneCoverType]): String =
    s"""
       |"options": {
       |  ${samsungGalaxyCaseModels(models)},
       |  ${samsungGalaxyCoverTypes(coverTypes)}
       |}
     """.stripMargin

  def samsungGalaxyCaseModels(models: Seq[PhoneModel]): String =
    s"""
       |"models": {
       |  "option_name": "phone_model",
       |  "option_label": "Model",
       |  "options": [
       |    ${models.map(modelJson).mkString(",")}
       |  ],
       |  "singular": false
       |}
     """.stripMargin

  def samsungGalaxyCoverTypes(coverTypes: Seq[PhoneCoverType]): String =
    s"""
       |"cover_types": {
       |  "option_name": "cover_type",
       |  "option_label": "Cover Type",
       |  "options": [
       |    ${coverTypes.map(coverJson).mkString(",")}
       |  ]
       |}
     """.stripMargin

  def framedPrintProductJson(product: Product): String = {
    val config = product.config.get.asInstanceOf[FramedPrintConfig]
    val frameStyles = config.frameStyles
    val frameColours = config.frameColours
    val matteColours = config.matteColours
    s"""
       |{
       |  "data": {
       |    "products": [
       |      {
       |        "id": "${product.id}",
       |        "work_id": ${product.workId},
       |        "product_info": [
       |          ${product.info.map(productInfoJson).mkString(",")}
       |        ],
       |        "ia_code": "${product.definition.typeId}",
       |        "display_name": "${product.definition.displayName}",
       |        "product_image": ${productImageJson(product.images.head)},
       |        "product_images": [
       |           ${product.images.map(productImageJson).mkString(",")}
       |         ],
       |        "product_page": "${product.webLink.toString}",
       |        "price": {
       |           "currency": "${product.price.currency.isoCode}",
       |           "amount": "${product.price.amount.toString}"
       |        },
       |        ${framedPrintOptionsJson(config, frameStyles, frameColours, matteColours)}
       |      }
       |    ]
       |  }
       |}
    """.stripMargin
  }

  def framedPrintOptionsJson(config: FramedPrintConfig, frameStyles: Seq[PrintFrameStyle],
      frameColours: Seq[FrameColour], matteColours: Seq[MatteColour]): String =
    s"""
       |"options": {
       |  ${framedPrintSizesJson(config.sizes)},
       |  ${framedPrintFrameStylesJson(frameStyles)},
       |  ${framedPrintFrameColoursJson(frameColours)},
       |  ${framedPrintMatteColours(matteColours)}
       |}
     """.stripMargin

  def framedPrintSizesJson(sizes: Seq[ProductSize]): String =
    s"""
       |"sizes": {
       |  "option_name": "size",
       |  "option_label": "Size",
       |  "options": [ ${sizes.map(sizeJson).mkString(",")} ]
       |}
     """.stripMargin

  def framedPrintFrameStylesJson(styles: Seq[PrintFrameStyle]): String =
    s"""
       |"frame_styles": {
       |  "option_name": "frame_style",
       |  "option_label": "Frame Style",
       |  "options": [
       |    ${styles.map(framedPrintStyleJson).mkString(",")}
       |  ]
       |}
     """.stripMargin

  def framedPrintStyleJson(s: PrintFrameStyle): String =
    s"""
       |{
       |  "value": "${s.value}",
       |  "display_name": "${s.valueDisplayName}",
       |  "selected": ${s.defaultSelection}
       |}
     """.stripMargin

  def framedPrintFrameColoursJson(colours: Seq[FrameColour]): String =
    s"""
       |"frame_colors": {
       |  "option_name": "frame_color",
       |  "option_label": "Frame Color",
       |  "options": [
       |    ${colours.map(namedColourJson).mkString(",")}
       |  ]
       |}
     """.stripMargin

  def framedPrintMatteColours(colours: Seq[MatteColour]): String =
    s"""
       |"matte_colors": {
       |  "option_name": "matte_color",
       |  "option_label": "Matte Color",
       |  "options": [
       |    ${colours.map(namedColourJson).mkString(",")}
       |  ]
       |}
     """.stripMargin

  def tshirtProductJson(product: Product): String = {
    val config = product.config.get.asInstanceOf[ApparelConfig]
    s"""
       |{
       |  "data": {
       |    "products": [
       |      {
       |        "id": "${product.id}",
       |        "work_id": ${product.workId},
       |        "product_info": [
       |          ${product.info.map(productInfoJson).mkString(",")}
       |        ],
       |        "ia_code": "${product.definition.typeId}",
       |        "display_name": "${product.definition.displayName}",
       |        "product_image": ${productImageJson(product.images.head)},
       |        "product_images": [
       |           ${product.images.map(productImageJson).mkString(",")}
       |         ],
       |        "product_page": "${product.webLink.toString}",
       |        "price": {
       |           "currency": "${product.price.currency.isoCode}",
       |           "amount": "${product.price.amount.toString}"
       |        },
       |        ${tshirtProductOptionsJson(config)},
       |        ${tshirtSizingDetailsJson(config.sizes)}
       |      }
       |    ]
       |  }
       |}
    """.stripMargin
  }

  def tshirtSizingDetailsJson(sizes: Seq[ProductSize]): String =
    s"""
       |"sizing_details": {
       |  "measurements": [
       |    ${sizes.map(_.measurement.get.asInstanceOf[ApparelSizeMeasurement]).map(apparelMeasurementJson).mkString(",")}
       |  ]
       |}
     """.stripMargin

  def apparelMeasurementJson(measurement: ApparelSizeMeasurement): String =
    s"""
       |{
       |  "label": "${measurement.label}",
       |  "chest": "${measurement.chest}",
       |  "length": "${measurement.length}"
       |}
     """.stripMargin

  def tshirtProductOptionsJson(config: ApparelConfig): String =
    s"""
       |"options": {
       |  ${tshirtSizesJson(config.sizes)},
       |  ${tshirtColoursJson(config.colours)},
       |  ${tshirtNonDisplayableOptionsJson(config.nonDisplayableConfig)}
       |}
     """.stripMargin

  def tshirtNonDisplayableOptionsJson(config: Seq[FlattenedConfigItem]): String =
    config.map(item => s""""${item.name}": "${item.value}"""").mkString(",")

  def modelJson(model: PhoneModel): String =
    s"""
       |{
       |  "value": "${model.value}",
       |  "display_name": "${model.valueDisplayName}",
       |  "selected": ${model.defaultSelection}
       |}
    """.stripMargin

  def coverJson(cover: PhoneCoverType): String =
    s"""
       |{
       |  "value": "${cover.value}",
       |  "display_name": "${cover.valueDisplayName}",
       |  "selected": ${cover.defaultSelection}
       |}
    """.stripMargin

  def sizeJson(size: ProductSize): String =
    s"""
       |{
       |  "value": "${size.value}",
       |  "display_name": "${size.valueDisplayName}",
       |  "selected": ${size.defaultSelection}
       |}
    """.stripMargin

  def tshirtSizesJson(sizes: Seq[ProductSize]): String =
    s"""
       |"sizes": {
       |  "option_name": "size",
       |  "option_label": "Size",
       |  "options": [ ${sizes.map(sizeJson).mkString(",")} ]
       |}
     """.stripMargin

  def tshirtColoursJson(colours: Seq[ApparelColour]): String =
    s"""
       |"body_color": [ ${colours.map(colourWithHexJson).mkString(",")} ]
     """.stripMargin

  def namedColourJson(colour: ProductColour): String =
    s"""
       |{
       |  "value": "${colour.value}",
       |  "display_name": "${colour.valueDisplayName}",
       |  "selected": ${colour.defaultSelection}
       |}
     """.stripMargin

  def colourWithHexJson(colour: ApparelColour): String =
    s"""
       |{
       |  "value": "${colour.value}",
       |  "display_name": "${colour.valueDisplayName}",
       |  "selected": ${colour.defaultSelection},
       |  "hex": "${colour.colour}"
       |}
     """.stripMargin

  def extraConfigJson(extra: FlattenedConfigItem): String =
    s"""
       |{
       |  "value": "${extra.value}",
       |  "display_name": "${extra.valueDisplayName}",
       |  "selected": ${extra.defaultSelection}
       |}
     """.stripMargin
}
