package com.redbubble.gql.fetch.people

import cats.data.NonEmptyList
import com.redbubble.gql.backends.people.PeopleBackend
import com.redbubble.gql.fetch.DataSourceOps._
import com.redbubble.gql.services.people.Person
import fetch._

object PeopleDataSource extends DataSource[Unit, Seq[Person]] {
  override def name = "People"

  override def identity(i: Unit) = (name, "AllPeople")

  override def fetchOne(id: Unit) = asyncQuery(PeopleBackend.allPeople())

  override def fetchMany(ids: NonEmptyList[Unit]) = batchingNotSupported(ids)
}
