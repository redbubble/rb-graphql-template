package com.redbubble.gql.graphql

import com.redbubble.gql.services.artists.ArtistsService
import com.redbubble.gql.services.cart.CartService
import com.redbubble.gql.services.delivery.DeliveryService
import com.redbubble.gql.services.device.DevicesService
import com.redbubble.gql.services.feed.FeedsService
import com.redbubble.gql.services.knowledgegraph.KnowledgeGraphService
import com.redbubble.gql.services.locale.LocalesService
import com.redbubble.gql.services.product.ProductsService
import com.redbubble.gql.services.search.SearchService
import com.redbubble.gql.services.topics.TopicsService
import com.redbubble.gql.services.works.WorksService

trait RootContext extends FeedsService
    with LocalesService
    with WorksService
    with ProductsService
    with SearchService
    with DevicesService
    with CartService
    with ArtistsService
    with DeliveryService
    with KnowledgeGraphService
    with TopicsService

object RootContext extends RootContext
