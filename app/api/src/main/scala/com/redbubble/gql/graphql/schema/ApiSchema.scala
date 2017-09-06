package com.redbubble.gql.graphql.schema

import com.redbubble.gql.graphql.schema.query.QueryApi._
import sangria.schema._

object ApiSchema {
  val schema = Schema(
    query = QueryType
  )
}
