package com.redbubble.gql.product

import java.net.URL

import com.redbubble.gql.core.{Id, Identifiable}
import com.redbubble.gql.magickraum.WorkImageDefinition
import com.redbubble.gql.services.artists.Artist
import com.redbubble.gql.services.product._

final case class WorkWithFeaturedProducts(work: Work, products: FeaturedProducts) extends Identifiable {
  override val id = work.id
}

final case class Work(workId: WorkId, title: WorkTitle, artist: Artist, imageDefinition: WorkImageDefinition, webLink: URL)
    extends Identifiable {
  override val id = Id(workId)
}
