package com.redbubble.gql.api.v1.graphql

import com.redbubble.graphql.GraphQlQuery
import com.redbubble.graphql.GraphQlQueryDecoders._
import com.redbubble.util.http.RequestOps
import io.finch.Decode

trait GraphQlRequestDecoders {
  implicit val graphQlQueryDecode: Decode.Json[GraphQlQuery] = RequestOps.decodeRootJson[GraphQlQuery](queryDecoder, cleanJson)
}

object GraphQlRequestDecoders extends GraphQlRequestDecoders
