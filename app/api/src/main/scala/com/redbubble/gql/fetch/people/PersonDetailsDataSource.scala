package com.redbubble.gql.fetch.people

import cats.data.NonEmptyList
import com.redbubble.gql.backends.people.PeopleBackend
import com.redbubble.gql.fetch.DataSourceOps._
import com.redbubble.gql.services.people.Person
import fetch._

object PersonDetailsDataSource extends DataSource[PeopleId, Option[Person]] {
  override def name = "PersonDetails"

  override def identity(i: PeopleId) = (name, i.identity)

  override def fetchOne(id: PeopleId) = asyncQuery(PeopleBackend.personDetails(id.personId))

  override def fetchMany(ids: NonEmptyList[PeopleId]) = batchingNotSupported(ids)
}
