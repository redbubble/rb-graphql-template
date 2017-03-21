package com.redbubble.gql.api

import com.redbubble.gql.api.v1.explore.ExploreApi.exploreApi
import com.redbubble.gql.api.v1.graphql.GraphQlApi.graphQlApi
import com.redbubble.gql.api.v1.health.HealthApi.healthApi

package object v1 {
  val api = "v1" :: (exploreApi :+: graphQlApi :+: healthApi)
}
