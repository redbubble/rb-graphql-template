package com.redbubble.gql.backends.people

import com.redbubble.gql.services.people.{BirthYear, HairColour, Name, Person}
import com.redbubble.util.json.syntax._
import io.circe.Decoder

trait PeopleDecoders {
  val personDecoder: Decoder[Person] = Decoder.instance { c =>
    for {
      name <- c.downField("name").as[String].map(Name)
      birthYear <- c.downField("birth_year").as[String].map(BirthYear)
      hairColour <- c.downField("hair_color").as[String].map(HairColour)
    } yield Person(name, birthYear, hairColour)
  }

  val peopleDecoder: Decoder[Seq[Person]] = Decoder.instance { c =>
    for {
      people <- c.downField("results").as[Seq[Person]](personDecoder.seqDecoder)
    } yield people
  }
}

object PeopleDecoders extends PeopleDecoders
