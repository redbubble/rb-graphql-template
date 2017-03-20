package com.redbubble.gql.graphql.schema.types

import com.redbubble.gql.graphql.schema.resolvers.AddToCart.InputFieldName
import com.redbubble.gql.graphql.schema.types.InputTypes.{FieldQuantity, FieldTypeId, FieldWorkId, StringKeyValuePairType}
import sangria.schema.InputObjectType.DefaultInput
import sangria.schema.{Argument, InputField, InputObjectType, ListInputType}

object CartTypes {
  val FieldConfig = InputField("config", ListInputType(StringKeyValuePairType), "The selected configuration for a product.")

  val CartProductType: InputObjectType[DefaultInput] =
    InputObjectType(
      name = "CartProduct",
      description = "Details of a product, for adding to a cart.",
      fields = List(FieldWorkId, FieldTypeId, FieldQuantity, FieldConfig)
    )

  val FieldProducts = InputField("products", ListInputType(CartProductType), "The products to add to a cart.")

  val BulkAddToCartType: InputObjectType[DefaultInput] =
    InputObjectType(
      name = "BulkAddToCart",
      description = "Bulk add to cart fields.",
      fields = List(FieldProducts)
    )

  val BulkAddToCartArg = Argument(InputFieldName, BulkAddToCartType, "Bulk add to cart arguments.")
}
