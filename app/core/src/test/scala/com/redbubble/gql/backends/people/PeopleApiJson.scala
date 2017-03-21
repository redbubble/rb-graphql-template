package com.redbubble.gql.backends.people

import com.redbubble.gql.services.people.Person

trait PeopleApiJson {
  def peopleJson(people: Seq[Person]): String =
    s"""
       |{
       |    "count": ${people.length},
       |    "next": "http://swapi.co/api/people/?page=2",
       |    "previous": null,
       |    "results": [${people.map(personJson).mkString(",")}]
       |}
     """.stripMargin

  def personJson(person: Person): String =
    s"""
       |{
       |    "name": "${person.name}",
       |    "height": "172",
       |    "mass": "77",
       |    "hair_color": "${person.hairColour}",
       |    "skin_color": "fair",
       |    "eye_color": "blue",
       |    "birth_year": "${person.birthYear}",
       |    "gender": "male",
       |    "homeworld": "http://swapi.co/api/planets/1/",
       |    "films": [
       |        "http://swapi.co/api/films/6/",
       |        "http://swapi.co/api/films/3/",
       |        "http://swapi.co/api/films/2/",
       |        "http://swapi.co/api/films/1/",
       |        "http://swapi.co/api/films/7/"
       |    ],
       |    "species": [
       |        "http://swapi.co/api/species/1/"
       |    ],
       |    "vehicles": [
       |        "http://swapi.co/api/vehicles/14/",
       |        "http://swapi.co/api/vehicles/30/"
       |    ],
       |    "starships": [
       |        "http://swapi.co/api/starships/12/",
       |        "http://swapi.co/api/starships/22/"
       |    ],
       |    "created": "2014-12-09T13:50:51.644000Z",
       |    "edited": "2014-12-20T21:17:56.891000Z",
       |    "url": "http://swapi.co/api/people/1/"
       |}
     """.stripMargin
}
