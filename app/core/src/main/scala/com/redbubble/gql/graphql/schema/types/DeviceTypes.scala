package com.redbubble.gql.graphql.schema.types

import com.redbubble.graphql.ScalarTypes._
import com.redbubble.gql.services.device._
import com.redbubble.gql.graphql.schema.resolvers.DeviceRegistration.InputFieldName
import com.redbubble.gql.graphql.schema.types.AppTypes._
import com.redbubble.gql.graphql.schema.types.BaseTypes._
import com.redbubble.gql.services.device.{AppFeature, Device, DeviceStatus, RegisteredDevice}
import sangria.schema.InputObjectType.DefaultInput
import sangria.schema.{Field, ObjectType, OptionType, _}
import sangria.validation.ValueCoercionViolation

object DeviceTypes {

  //
  // BundleId
  //

  private case object BundleIdCoercionViolation extends ValueCoercionViolation(s"Bundle ID expected")

  private def parseBundleId(s: String): Either[BundleIdCoercionViolation.type, BundleId] = Right(BundleId(s))

  val BundleIdType =
    stringScalarType("BundleId", s"The bundle ID of an app.", parseBundleId, () => BundleIdCoercionViolation)

  val BundleIdArg =
    Argument("bundleId", BundleIdType, description = s"The bundle ID of an app.")

  //
  // PushNotificationToken
  //

  private case object PushNotificationTokenCoercionViolation
      extends ValueCoercionViolation(s"Push notification token expected")

  private def parseToken(s: String): Either[PushNotificationTokenCoercionViolation.type, PushNotificationToken] =
    Right(PushNotificationToken(s))

  val PushNotificationTokenType =
    stringScalarType(
      "PushNotificationToken", s"An iOS push notification token.",
      parseToken, () => PushNotificationTokenCoercionViolation
    )

  val PushNotificationTokenArg =
    Argument("token", PushNotificationTokenType, description = s"An iOS push notification token.")

  //
  // RawVersion
  //

  private case object RawVersionCoercionViolation
      extends ValueCoercionViolation(s"Software version expected")

  private def parseVersion(s: String): Either[RawVersionCoercionViolation.type, RawVersion] = Right(RawVersion(s))

  val RawVersionType =
    stringScalarType(
      "RawVersion", s"The (raw/unparsed) version of a piece of software, e.g. 1.0.0 build 81.",
      parseVersion, () => RawVersionCoercionViolation
    )

  val AppVersionArg =
    Argument("appVersion", RawVersionType, description = s"The version of an app, e.g. 1.0.0 build 81.")

  val OsVersionArg =
    Argument("osVersion",
      RawVersionType,
      description = s"The version of the operating system the device is running, e.g. 10.0 (14A5309d)"
    )

  val DeviceStatusType = ObjectType(
    "DeviceStatus", "Whether a device & the app it's running is supported or not.",
    fields[Unit, DeviceStatus](
      Field("status", StringType,
        Some(s"The status of the registered device; one of ${DeviceStatus.deviceStatuses.mkString(", ")}"),
        resolve = _.value.status),
      Field("reason", OptionType(StringType), Some("If deprecated or not supported, the reason why."), resolve = _.value.message)
    )
  )

  val AppFeatureType = ObjectType(
    "AppFeature", "The app features presented to this device.",
    fields[Unit, AppFeature](
      Field("id", StringType, Some("The identifier of the feature."), resolve = _.value.id),
      Field("enabled", BooleanType, Some("Whether this feature is enabled."), resolve = _.value.enabled)
    )
  )

  val DeviceType = ObjectType(
    "Device", "An iOS device.",
    interfaces[Unit, Device](IdentifiableType),
    fields[Unit, Device](
      Field("token", PushNotificationTokenType, Some("The push notification token for the device."), resolve = _.value.token),
      Field("app", AppType, Some("The app installed on a device."), resolve = _.value.app),
      Field("osVersion", SoftwareVersionType, Some("The version of the operating system installed on the device."), resolve = _.value.osVersion)
    )
  )

  val RegisteredDeviceType = ObjectType(
    "RegisteredDevice", "An iOS device.",
    interfaces[Unit, RegisteredDevice](IdentifiableType),
    fields[Unit, RegisteredDevice](
      Field("device", OptionType(DeviceType), Some("The device that was registered."), resolve = _.value.device),
      Field("supportStatus", DeviceStatusType, Some(s"The status of the registered device."), resolve = _.value.status),
      Field("features", ListType(AppFeatureType), Some(s"The app features presented to this device."), resolve = _.value.features)
    )
  )

  val FieldPushNotificationToken = InputField(
    "token",
    OptionInputType(PushNotificationTokenType),
    "If available, the push notification token for the device. May be empty if the user has not given permission to send notifications."
  )
  val FieldBundleId = InputField("bundleId", BundleIdType, "The bundle ID of the app.")
  val FieldAppVersion = InputField("appVersion", RawVersionType, "The version of the app, e.g. 1.0.0 build 81.")
  val FieldOsVersion = InputField("osVersion", RawVersionType, "The version of the operating system the device is running, e.g. 10.0 (14A5309d).")

  val RegisterDeviceType: InputObjectType[DefaultInput] =
    InputObjectType(
      name = "RegisterDevice",
      description = "Register device fields.",
      fields = List(FieldPushNotificationToken, FieldBundleId, FieldAppVersion, FieldOsVersion)
    )

  val RegisterDeviceArg = Argument(InputFieldName, RegisterDeviceType, "Register device fields.")
}
