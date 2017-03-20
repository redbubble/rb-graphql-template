package com.redbubble.gql.api.v1

import com.redbubble.gql.api.v1.graphql.GraphQlEncoders
import com.redbubble.util.http.ErrorEncoders

trait ResponseEncoders extends ErrorEncoders with GraphQlEncoders

object ResponseEncoders extends ResponseEncoders
