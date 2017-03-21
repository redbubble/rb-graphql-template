package com.redbubble.gql.graphql.schema.mutation

import com.redbubble.gql.graphql.RootContext
import sangria.schema.{ObjectType, _}

object MutationApi {

  val MutationType: ObjectType[RootContext, Unit] = ObjectType(
    "MutationAPI",
    description = "The Redbubble GraphQL Template Mutation API.",
    fields[RootContext, Unit]()
  )
}
