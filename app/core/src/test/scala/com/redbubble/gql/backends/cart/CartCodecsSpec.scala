package com.redbubble.gql.backends.cart

import com.redbubble.gql.backends.cart.CartCodecs._
import com.redbubble.gql.product._
import com.redbubble.gql.services.cart._
import com.redbubble.gql.services.product._
import com.redbubble.gql.util.spec.ModelGenerators
import com.redbubble.util.json.CodecOps
import com.redbubble.util.json.JsonPrinter._
import com.redbubble.util.spec.SpecHelper
import org.scalacheck.Prop._
import org.scalacheck.Properties
import org.specs2.mutable.Specification

final class CartCodecsSpec extends Specification with SpecHelper with ModelGenerators with CartBackendJson with CodecOps {
  val decodingProp = new Properties("Cart products encoding") {
    property("encoding") = forAll(genCartProduct) { (product: CartProduct) =>
      val expected = parse(cartProductsJson(Seq(product)))
      val actual = parse(jsonToBuf(cartProductsEncoder(Seq(product))))
      actual must beEqualTo(expected)
    }
  }

  s2"Encoding cart products$decodingProp"

  "Some products require special configuration" >> {
    "Phone cases" >> {
      val typeIds = Seq(iPhoneCase.typeId, GalaxyCase.typeId)
      val productsJson = typeIds.map { t =>
        jsonToString(cartProductsEncoder(Seq(product("123", t, Seq("phone_model" -> "m", "cover_type" -> "c")))))
      }
      productsJson must contain(allOf(contain(""" "type":"m_c" """.trim)))
    }

    "iPhone waller" >> {
      val typeIds = Seq(iPhoneWallet.typeId)
      val productsJson = typeIds.map { t =>
        jsonToString(cartProductsEncoder(Seq(product("123", t, Seq("phone_model" -> "m")))))
      }
      productsJson must contain(allOf(contain(""" "type":"m" """.trim)))
    }

    "Kids & baby clothing" >> {
      val typeIds = Seq(KidsTee.typeId, BabyTee.typeId, BabyShortSleeveOnePiece.typeId, BabyLongSleeveOnePiece.typeId)
      val productsJson = typeIds.map(t => jsonToString(cartProductsEncoder(Seq(product("123", t, Seq.empty)))))
      productsJson must contain(allOf(contain(""" "print_location":"front" """.trim)))
    }

    "Posters" >> {
      val typeIds = Seq(Poster.typeId)
      val productsJson = typeIds.map(t => jsonToString(cartProductsEncoder(Seq(product("123", t, Seq.empty)))))
      productsJson must contain(allOf(contain(""" "finish":"semi_gloss" """.trim)))
    }

    "Products with 'print_only_front' as an option" >> {
      val p = product("123", "does_not_matter", Seq("print_only_front" -> "does_not_matter"))
      val productJson = jsonToString(cartProductsEncoder(Seq(p)))
      productJson must contain(""" "print_location":"front" """.trim)
    }
  }

  private def product(workId: String, typeId: String, selectedConfig: Seq[(String, String)]) =
    CartProduct(WorkId(workId), SelectedProductConfig(ProductTypeId(typeId), selectedConfig), Quantity(1))
}
