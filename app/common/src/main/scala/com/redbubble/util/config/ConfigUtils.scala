package com.redbubble.util.config

trait ConfigUtils {
  final def envVar(name: String): Option[String] = sys.env.get(name)

  final def envVarOrFail(name: String): String = {
    envVar(name) match {
      case Some(e) => e
      case None => sys.error(s"Required environment variable '$name' not found")
    }
  }

  final def propertyVar(name: String): Option[String] = sys.props.get(name)

  final def propertyVarOrFail(name: String): String = {
    propertyVar(name) match {
      case Some(e) => e
      case None => sys.error(s"Required system property '$name' not found")
    }
  }
}

object ConfigUtils extends ConfigUtils
