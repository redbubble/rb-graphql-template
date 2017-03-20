package com.redbubble

import shapeless.tag
import shapeless.tag.@@

trait KeyIdTag

trait KeyTag

trait ExtendedDataTag

trait RawAuthenticationHeaderTag

trait HeaderKeyValueTag

trait HeaderKeyTag

trait HeaderValueTag

trait Base64EncodedTag

package object hawk {
  type KeyId = String @@ KeyIdTag
  type Key = String @@ KeyTag
  type ExtendedData = String @@ ExtendedDataTag
  type RawAuthenticationHeader = String @@ RawAuthenticationHeaderTag
  type HeaderKeyValue = String @@ HeaderKeyValueTag
  type HeaderKey = String @@ HeaderKeyTag
  type HeaderValue = String @@ HeaderValueTag
  type Base64Encoded = String @@ Base64EncodedTag

  val MustAuthenticateHttpHeader = "WWW-Authenticate"
  val AuthorisationHttpHeader = "Authorization"
  val HawkHeaderValuePrefix = "Hawk"

  def KeyId(s: String): @@[String, KeyIdTag] = tag[KeyIdTag](s)

  def Key(s: String): @@[String, KeyTag] = tag[KeyTag](s)

  def ExtendedData(s: String): @@[String, ExtendedDataTag] = tag[ExtendedDataTag](s)

  def RawAuthenticationHeader(s: String): @@[String, RawAuthenticationHeaderTag] = tag[RawAuthenticationHeaderTag](s)

  def HeaderKeyValue(s: String): @@[String, HeaderKeyValueTag] = tag[HeaderKeyValueTag](s)

  def HeaderKey(s: String): @@[String, HeaderKeyTag] = tag[HeaderKeyTag](s)

  def HeaderValue(s: String): @@[String, HeaderValueTag] = tag[HeaderValueTag](s)

  def Base64Encoded(s: String): @@[String, Base64EncodedTag] = tag[Base64EncodedTag](s)

  def errorE[T](message: String): Either[HawkError, T] = Left(error(message))

  def error(message: String): HawkError = new HawkError(message)
}
