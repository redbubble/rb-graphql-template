package com.redbubble.gql.services.people

import com.redbubble.gql.fetch.products.ProductsFetcher.productDetailsFetch
import com.redbubble.gql.fetch.people.PeopleFetcher._
import com.redbubble.gql.product.FeaturedProducts.filterFeatured
import com.redbubble.gql.product.SupportedProduct._
import com.redbubble.gql.product._
import com.redbubble.gql.services.locale.Locale
import com.redbubble.gql.services.product._
import com.redbubble.util.fetch.syntax._
import com.twitter.util.Future

trait PeopleService {
  private implicit lazy val fetchRunner = com.redbubble.gql.util.fetch.runner

  final def peopleDetails(personId: PersonId): Future[Option[Work]] = {
    val fetch = workFetch(personId)
    fetch.runF
  }
}

object PeopleService extends PeopleService
