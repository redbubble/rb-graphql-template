package com.redbubble.gql.graphql.schema.resolvers

import com.redbubble.graphql.GraphQlError.graphQlError
import com.redbubble.graphql.InputHelper
import com.redbubble.graphql.syntax._
import com.redbubble.gql.graphql.RootContext
import com.redbubble.gql.graphql.schema.types.DeviceTypes._
import com.redbubble.gql.services.device.SoftwareVersion.fromRawVersion
import com.redbubble.gql.services.device.{App, Device, RegisteredDevice}
import com.redbubble.util.async.syntax._
import com.twitter.util.Future
import sangria.schema._

object DeviceRegistration extends InputHelper {
  def registerDevice(ctx: Context[RootContext, Unit]): Action[RootContext, RegisteredDevice] = {
    val token = ctx.inputArg(FieldPushNotificationToken).flatten
    val registeredDevice = for {
      bundleId <- ctx.inputArg(FieldBundleId)
      appVersion <- ctx.inputArg(FieldAppVersion).flatMap(fromRawVersion)
      osVersion <- ctx.inputArg(FieldOsVersion).flatMap(fromRawVersion)
    } yield {
      val device = Device.device(token, App(bundleId, appVersion), osVersion)
      ctx.ctx.registerDevice(device)
    }
    registeredDevice.getOrElse(Future.exception(graphQlError("Unable to parse device input fields"))).asScala
  }
}
