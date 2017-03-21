package com.redbubble.gql.fetch.people

import com.redbubble.gql.services.people.{Person, PersonId}
import fetch.Fetch

object PeopleFetcher {
  def allPeopleFetch(): Fetch[Seq[Person]] = {
    val id = ()
    Fetch(id)(PeopleDataSource)
  }

  def personFetch(personId: PersonId): Fetch[Option[Person]] = {
    val id = PeopleId(personId)
    Fetch(id)(PersonDetailsDataSource)
  }
}
