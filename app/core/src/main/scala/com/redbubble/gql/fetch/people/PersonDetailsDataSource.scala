package com.redbubble.gql.fetch.people

import cats.data.NonEmptyList
import com.redbubble.gql.fetch.DataSourceOps._
import com.redbubble.gql.services.people.Person
import fetch._

object PersonDetailsDataSource extends DataSource[PeopleId, Option[Person]] {
  override def name = "PersonDetails"

  override def identity(i: PeopleId) = (name, i.identity)

  override def fetchOne(id: PeopleId) = asyncQuery(PeopleBackend.workDetails(id.workId, id.locale))

  override def fetchMany(ids: NonEmptyList[PeopleId]) = batchingNotSupported(ids)
}
