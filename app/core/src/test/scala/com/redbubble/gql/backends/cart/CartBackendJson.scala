package com.redbubble.gql.backends.cart

import com.redbubble.gql.services.cart.CartProduct

trait CartBackendJson {
  def cartProductsJson(products: Seq[CartProduct]): String =
    s"""
       |{
       |  "products": [
       |    ${products.map(cartProductJson).mkString(",")}
       |  ]
       |}
     """.stripMargin

  def cartProductJson(product: CartProduct): String = {
    val baseItems = Seq(
      s""" "work_id": "${product.workId}" """,
      s""" "ia_code": "${product.product.typeId}" """,
      s""" "quantity": "${product.quantity.toString}" """
    )
    val cartConfig = product.product.cartConfig.map(t => s""" "${t._1}": "${t._2}" """)
    val configItems = product.product.selectedConfig.map(c => s""" "${c._1}":"${c._2}" """)
    s"""
       |{
       |  ${(baseItems ++ configItems ++ cartConfig).map(_.trim).mkString(",")}
       |}
     """.stripMargin
  }
}
