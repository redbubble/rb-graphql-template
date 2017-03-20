package com.redbubble.gql.graphql.schema.types

import com.redbubble.gql.core._
import sangria.schema.{Field, ObjectType, _}

object BaseTypes {
  //
  // Generic helpers/utilities
  //

  def unionTypeNames(types: List[ObjectType[Unit, _]]): String = types.map(_.valClass.getSimpleName).mkString(", ")

  def identifiableType[T]: InterfaceType[T, Identifiable] = InterfaceType(
    name = "Identifiable",
    fields = fields[T, Identifiable](
      Field(
        name = "id",
        fieldType = IDType,
        description = Some("The unique identifier of the instance of this type."),
        resolve = _.value.id
      )
    )
  )

  val IdentifiableType = identifiableType[Unit]
}
