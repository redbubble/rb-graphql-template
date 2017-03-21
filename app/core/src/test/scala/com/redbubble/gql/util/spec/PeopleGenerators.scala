package com.redbubble.gql.util.spec

import com.redbubble.gql.services.people.{BirthYear, HairColour, Name, Person}
import com.redbubble.util.spec.gen.GenHelpers
import org.scalacheck.Gen

trait PeopleGenerators extends GenHelpers {
  val genName: Gen[Name] = genNonEmptyString.map(Name)
  val genHairColour: Gen[HairColour] = genNonEmptyString.map(HairColour)
  val genBirthYear: Gen[BirthYear] = genNonEmptyString.map(BirthYear)

  val genPerson: Gen[Person] = for {
    n <- genName
    c <- genHairColour
    y <- genBirthYear
  } yield Person(n, y, c)
}
