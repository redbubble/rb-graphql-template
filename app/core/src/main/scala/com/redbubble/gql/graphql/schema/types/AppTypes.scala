package com.redbubble.gql.graphql.schema.types

import com.redbubble.gql.services.device.{App, SoftwareVersion}
import sangria.schema.{Field, ObjectType, OptionType, _}

object AppTypes {
  val SoftwareVersionType = ObjectType(
    "SoftwareVersion", "The version of a piece of software.",
    fields[Unit, SoftwareVersion](
      Field("major", StringType, Some("The major version of the software."), resolve = _.value.major),
      Field("minor", StringType, Some("The minor version of the software."), resolve = _.value.minor),
      Field("patch", StringType, Some("The patch version of the software."), resolve = _.value.patch),
      Field("build", OptionType(StringType), Some("The build of the software."), resolve = _.value.buildVersion),
      Field("raw", StringType, Some("The 'raw' version of the software."), resolve = _.value.raw),
      Field("canonical", StringType, Some("The canonical or full version of the software."), resolve = _.value.canonical)
    )
  )

  val AppType = ObjectType(
    "App", "An app installed on a device.",
    fields[Unit, App](
      Field("bundleId", StringType, Some("The bundle ID of the application."), resolve = _.value.bundleId),
      Field("version", SoftwareVersionType, Some("The version of the application."), resolve = _.value.appVersion)
    )
  )
}
