# HAWK Support for Finagle/Finch

[HTTP Holder-Of-Key Authentication Scheme](https://github.com/hueniverse/hawk) (Hawk) support for [Finagle](https://github.com/finagle/finagle).

Note that it currently doesn't support [Response Payload Validation](https://github.com/hueniverse/hawk#response-payload-validation).

Almost all of this library is Finagle agnostic, only `HawkAuthenticateRequestFilter` is tied to Finagle.

The code is almost directly pulled from a production codebase, no effort was made to make it resusable, sorry. It might not compile without some changes. In particular, you'll need to include:

* An implementation of `AuthenticationFailedError`.
* A sample `Time` implementation has been provided.

See [Misc](#misc) for an example implementation of these classes.

# Setup

You will need to add something like the following to your `build.sbt`:

```
libraryDependencies ++= Seq(
  "com.twitter" %% "finagle-http" % "6.35.0"
  "org.typelevel" %% "cats-core" % "0.6.0",
  "joda-time" % "joda-time" % "2.9.3",
  "org.joda" % "joda-convert" % "1.8",
  "com.github.benhutchison" %% "mouse" % "0.2"
)
```

# Usage

```scala
val creds = Credentials(KeyId("Key ID"), Key("8e2dd2949b0e30c544336f73f94e2df3"), Sha256)

object AuthenticationFilter extends HawkAuthenticateRequestFilter(creds)

val authenticatedService = AuthenticationFilter andThen service
```

# Misc

We make use of a `AuthenticationFailedError` error, which is hooked up to a top-level exception handling filter in Finagle. It looks something like this:

```scala
abstract class ApiError extends Exception

abstract class ApiError_(message: String, cause: Option[Throwable]) extends ApiError {
  override def getMessage: String = message

  override def getCause: Throwable = cause.orNull
}

final case class AuthenticationFailedError(message: String, cause: Option[Throwable] = None) extends ApiError_(message, cause)
```

