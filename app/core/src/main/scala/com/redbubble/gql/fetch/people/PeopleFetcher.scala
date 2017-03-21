package com.redbubble.gql.fetch.people

import com.redbubble.gql.services.people.{Person, PersonId}
import fetch.Fetch

object PeopleFetcher {
  def workFetch(personId: PersonId): Fetch[Option[Person]] = {
    val id = PeopleId(personId)
    Fetch(id)(PersonDetailsDataSource)
  }
}
