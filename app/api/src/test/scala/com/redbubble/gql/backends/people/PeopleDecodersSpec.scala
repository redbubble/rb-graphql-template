package com.redbubble.gql.backends.people

import com.redbubble.gql.services.people.Person
import com.redbubble.gql.util.spec.{GqlGenerators, SpecHelper}
import org.scalacheck.Prop._
import org.scalacheck.Properties
import org.specs2.mutable.Specification

final class PeopleDecodersSpec extends Specification with SpecHelper with GqlGenerators with PeopleApiJson {
  val decodingProp = new Properties("People decoding") {
    property("decoding a single person") = forAll(genPerson) { (person: Person) =>
      val json = personJson(person)
      val decoded = decode(json)(PeopleDecoders.personDecoder)
      decoded must beRight(person)
    }

    property("decoding a list of people") = forAll(genPerson) { (person: Person) =>
      val json = peopleJson(Seq(person))
      val decoded = decode(json)(PeopleDecoders.peopleDecoder)
      decoded must beRight(Seq(person))
    }
  }

  s2"Decoding people responses$decodingProp"
}
