package com.redbubble.gql.graphql.schema.query

import com.redbubble.gql.graphql.RootContext
import com.redbubble.gql.graphql.schema.types.PeopleTypes._
import com.redbubble.util.async.syntax._
import sangria.schema.{Field, ListType, OptionType}

trait PeopleApi {
  final val allPeopleField: Field[RootContext, Unit] = Field(
    name = "allPeople",
    arguments = Nil,
    fieldType = ListType(PersonType),
    description = Some("All the people in the Star Wars universe."),
    resolve = _.ctx.allPeople().asScala

  )
  final val personField: Field[RootContext, Unit] = Field(
    name = "personDetails",
    arguments = List(PersonIdArg),
    fieldType = OptionType(PersonType),
    description = Some("A single specific person in the Star Wars universe."),
    resolve = ctx => ctx.ctx.personDetails(ctx.arg(PersonIdArg)).asScala
  )
}
