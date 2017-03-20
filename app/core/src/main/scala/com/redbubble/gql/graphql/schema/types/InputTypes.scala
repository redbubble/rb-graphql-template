package com.redbubble.gql.graphql.schema.types

import com.redbubble.gql.graphql.schema.types.WorkTypes._
import sangria.schema.InputObjectType._
import sangria.schema.{InputField, ListInputType, _}

object InputTypes {
  //
  // Common types
  //

  val StringKvKeyType = InputField("key", StringType, "A key, in a key/value pair.")
  val StringKvValueType = InputField("value", StringType, "A value, in a key/value pair.")

  val StringKeyValuePairType = InputObjectType(
    name = "KeyValuePair",
    description = "A generic container for `String` -> `String` key/value pairs (tuples).",
    fields = List(StringKvKeyType, StringKvValueType)
  )

  //
  // Work fields
  //

  val FieldWorkId = InputField("workId", WorkIdType, "The ID of a work.")

  //
  // Product fields
  //

  val FieldTypeId = InputField("typeId", StringType, "The identifier for the type of product.")
  val FieldQuantity = InputField("quantity", IntType, "The quantity of a product.")
  val FieldSelectedConfig = InputField("selectedConfig", ListInputType(StringKeyValuePairType), "Selected configuration for a product.")

  //
  // Selected product config
  //

  val SelectedProductConfigType: InputObjectType[DefaultInput] =
    InputObjectType(
      name = "SelectedProductConfig",
      description = "The selected configuration for a product.",
      fields = List(FieldTypeId, FieldSelectedConfig)
    )

  val SelectedProductConfigArg = Argument("selected", SelectedProductConfigType, "The selected configuration for a product.")
}
