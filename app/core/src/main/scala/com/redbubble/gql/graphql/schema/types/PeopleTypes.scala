package com.redbubble.gql.graphql.schema.types

import cats.syntax.either._
import com.redbubble.gql.services.people.{Person, PersonId}
import com.redbubble.graphql.ScalarTypes.stringScalarType
import mouse.string._
import sangria.marshalling.ToInput.ScalarToInput
import sangria.schema._
import sangria.validation.ValueCoercionViolation

object PeopleTypes {
  //
  // Person Id
  //

  implicit val personIdInput = new ScalarToInput[PersonId]

  private case object PersonIdCoercionViolation extends ValueCoercionViolation(s"Person ID expected.")

  private def parsePersonId(id: String): Either[PersonIdCoercionViolation.type, PersonId] =
    id.parseInt.map(PersonId).leftMap(_ => PersonIdCoercionViolation)

  val PersonIdType: ScalarType[PersonId] = stringScalarType(
    typeName = "PersonId",
    description = s"The ID of a person.",
    value = parsePersonId, error = () => PersonIdCoercionViolation
  )

  val PersonIdArg = Argument(
    name = "personId",
    argumentType = PersonIdType,
    description = s"The ID of a person."
  )

  //noinspection ScalaStyle
  val PersonType = ObjectType(
    "Person", "An individual person or character within the Star Wars universe.",
    fields[Unit, Person](
      Field(
        name = "name",
        fieldType = StringType,
        description = Some("The name of this person."),
        resolve = _.value.name
      ),
      Field(
        name = "birthYear",
        fieldType = StringType,
        description = Some(" The birth year of the person, using the in-universe standard of BBY or ABY - Before the Battle of Yavin or After the Battle of Yavin. The Battle of Yavin is a battle that occurs at the end of Star Wars episode IV: A New Hope."),
        resolve = _.value.birthYear
      ),
      Field(
        name = "hairColour",
        fieldType = StringType,
        description = Some("""The hair color of this person. Will be "unknown" if not known or "n/a" if the person does not have hair."""),
        resolve = _.value.hairColour
      )
    )
  )
}
