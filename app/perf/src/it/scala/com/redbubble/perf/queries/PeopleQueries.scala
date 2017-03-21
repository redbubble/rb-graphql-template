package com.redbubble.perf.queries

import com.redbubble.gql.services.people.PersonId

trait ProductQueries {
  val allPeopleQuery: String =
    s"""
       |{
       |  allPeople {
       |    name
       |    birthYear
       |    hairColour
       |  }
       |}
     """.stripMargin

  def personDetailsQuery(personId: PersonId): String =
    s"""
       |{
       |  personDetails(personId: "$personId") {
       |    name
       |    birthYear
       |    hairColour
       |  }
       |}
     """.stripMargin
}

object ProductQueries extends ProductQueries
