package com.redbubble.gql.product

import com.redbubble.gql.services.product._

final case class SelectedProductConfig(typeId: ProductTypeId, selectedConfig: Seq[(String, String)]) {
  /**
    * Configuration that we provide solely to the cart APIs, and only for certain products.
    *
    * We need to provide special options to the cart APIs for certain products. Some of these we hard code, some we
    * generate by combining other options.
    */
  lazy val cartConfig: Seq[(String, String)] = productSpecificConfig ++ printLocationConfig

  private def productSpecificConfig: Seq[(String, String)] = typeId match {
    case iPhoneCase.typeId | GalaxyCase.typeId =>
      value("phone_model").flatMap(model => value("cover_type").map(cover => Seq("type" -> s"${model}_$cover"))).getOrElse(Nil)
    case iPhoneWallet.typeId =>
      value("phone_model").map(model => Seq("type" -> model)).getOrElse(Nil)
    case KidsTee.typeId | BabyTee.typeId | BabyShortSleeveOnePiece.typeId | BabyLongSleeveOnePiece.typeId =>
      Seq("print_location" -> "front")
    case Poster.typeId =>
      Seq("finish" -> "semi_gloss")
    case _ => Nil
  }

  // If the product has "print_only_front" as an option, transform this to "print_location=front"
  private def printLocationConfig: Seq[(String, String)] =
    value("print_only_front").map(_ => Seq("print_location" -> "front")).getOrElse(Nil)

  private def value(key: String): Option[String] = selectedConfig.find(_._1 == key).map(_._2)
}
