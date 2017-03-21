package com.redbubble.gql.graphql.schema.types

import sangria.schema.{InputField, _}

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
}
