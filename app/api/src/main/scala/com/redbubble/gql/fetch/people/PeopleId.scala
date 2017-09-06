package com.redbubble.gql.fetch.people

import com.redbubble.gql.services.people.PersonId

final case class PeopleId(personId: PersonId) {
  val identity: String = s"PeopleId-$personId"
}
