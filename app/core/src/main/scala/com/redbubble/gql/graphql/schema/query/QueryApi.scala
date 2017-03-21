package com.redbubble.gql.graphql.schema.query

import com.redbubble.gql.graphql.RootContext
import sangria.schema.{ObjectType, _}

object QueryApi extends PeopleApi {

  val QueryType: ObjectType[RootContext, Unit] = ObjectType(
    "QueryAPI",
    description = "The Redbubble GraphQL Template Query API.",
    fields = fields[RootContext, Unit](
      allPeopleField,
      personField
    )
  )
}
