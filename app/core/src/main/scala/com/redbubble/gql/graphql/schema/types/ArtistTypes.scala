package com.redbubble.gql.graphql.schema.types

import com.redbubble.graphql.ScalarTypes.stringScalarType
import com.redbubble.gql.graphql.RootContext
import com.redbubble.gql.graphql.schema.types.BaseTypes._
import com.redbubble.gql.graphql.schema.types.ImageTypes._
import com.redbubble.gql.graphql.schema.types.LocaleTypes._
import com.redbubble.gql.graphql.schema.types.WorkTypes._
import com.redbubble.gql.services.artists.{Artist, Username}
import com.redbubble.gql.services.locale.Locale._
import com.redbubble.util.async.syntax._
import sangria.marshalling.ToInput.ScalarToInput
import sangria.schema._
import sangria.validation.ValueCoercionViolation

object ArtistTypes {
  //
  // Username
  //

  private implicit val usernameInput = new ScalarToInput[Username]

  private case object UsernameCoercionViolation extends ValueCoercionViolation(s"Username expected")

  private def parseUsername(u: String): Either[UsernameCoercionViolation.type, Username] = Right(Username(u))

  val UsernameType: ScalarType[Username] = stringScalarType(
    "Username",
    "A username for an artist.",
    parseUsername, () => UsernameCoercionViolation
  )

  val UsernameArg = Argument("username", UsernameType, "A username for an artist.")

  val ArtistType: ObjectType[Unit, Artist] = ObjectType(
    "Artist", "An artist.",
    interfaces[Unit, Artist](IdentifiableType),
    fields[Unit, Artist](
      Field("username", StringType, Some("The artist's username."), resolve = _.value.username),
      Field("name", StringType, Some("The artist's name."), resolve = _.value.name),
      Field("location", OptionType(StringType), Some("The artist's location."), resolve = _.value.location),
      Field(
        name = "avatar",
        fieldType = OptionType(StringType),
        description = Some("The url of the artist's avatar."),
        arguments = List(WidthArg, HeightArg),
        resolve = _.value.avatar.toString
      ),
      Field(
        name = "avatarUrl",
        fieldType = OptionType(StringType),
        description = Some("The url of the artist's avatar."),
        arguments = List(WidthArg, HeightArg),
        resolve = _.value.avatar.toString
      ),
      Field(
        name = "works",
        arguments = List(CountryCodeArg, CurrencyCodeArg, LanguageCodeArg),
        fieldType = ListType(WorkType),
        description = Some("The works created by this artist."),
        resolve = ctx => {
          val locale = localeForCodes(ctx.arg(CountryCodeArg), ctx.arg(CurrencyCodeArg), ctx.arg(LanguageCodeArg))
          RootContext.worksByArtist(ctx.value.username, locale).asScala
        }
      )
    )
  )
}
