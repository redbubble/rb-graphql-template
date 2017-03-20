# GraphQL Template API

# Overview

This is API for the GraphQL Template service.

# Conventions

* All calls run over TLS (in production).
* The API accepts & returns JSON, all `POST` & `PUT` payloads are specified in JSON (i.e. not `application/x-www-form-urlencoded`).
* All calls accept (for `PUT` & `POST`) & return JSON objects, e.g. `{"data":{"foo":"bar"}}`
* All requests must conform to GraphQL standards, e.g. contain at least `query` element. Because of this, requests
  *do not* contain a top-level `data` element.
* All responses return JSON objects, which will one of, and in some case both:
  * `errors` - if an error ocurred (contains an array of errors).
  * `data` - if the response was a success.
* Additional parameters may be specified in the query string where documented.
* All time stamps are [ISO 8601](http://www.w3.org/TR/NOTE-datetime) strings.
* Appropriate status codes are used, not limited to:
  * `200` - OK
  * `201` - New resource created
  * `401` - Unauthorized
  * `404` - Not found
* Where possible, this API attempts to conform to GraphQL convention (which may thus be undocumented).

# Authentication

In production, all requests require authentication using [Hawk](https://github.com/hueniverse/hawk), which is a HMAC
style authentication method. Provide an `Authorization` header containing the authorisation details.

```
GET /resource/1?b=1&a=2 HTTP/1.1
Host: example.com:8000
Authorization: Hawk id="dh37fgj492je", ts="1353832234", nonce="j4h3g2", ext="some-app-ext-data", mac="6R4rV5iE+NPoym+WwjeHzjAGXUtLNIxmo1vpMofpLAE="
```

If you don't include correct authentication you will get a `401` with a `WWW-Authenticate: Hawk` header:

```
$ curl -i -X POST --data '{}' "https://rb-graphql-template.herokuapp.com/v1/graphql"
HTTP/1.1 401 Unauthorized
Server: Cowboy
Date: Tue, 01 Nov 2016 02:24:54 GMT
Connection: keep-alive
Content-Length: 25
Content-Language: en
Content-Type: text/plain
Via: 1.1 vegur

Request is not authorised
```

# Required Parameters

All API operations expect the following params in their JSON payload (at the root level):

* `query`: The GraphQL query, encoded as a JSON string.

You may also supply the following:

* `variables` -  A JSON object containing the variables referenced in the query.
* `operationName` - If supplying multiple operations, the name of the operation to execute, as a JSON string.

# Errors

All errors from the API return a top level `errors` array. Each entry in the array contains information about the error.
It will include at least the following (but possibly more) fields:

* `message` - A human readable overview of the error (the exception message).

You may also get the following fields:

* `type` - The type of the exception.
* `cause` - If the exception has an underlying cause, its message.
* `location` - The line and column where the error occured.

Note that due to the way GraphQL works, you may sometimes get back both `data` and an `errors` top level fields, with a
`200` status code. In this case it is up to the client to determine whether this represents failue or not.

For example, a malformed request:

```
$ curl -i -X POST --data '{ "query" : "{ volumes { number1 } }" }' "http://rb-graphql-template/v1/graphql"
HTTP/1.1 400 Bad Request
Content-Type: application/json
Content-Length: 200

{"errors":[{"message":"Cannot query field 'number1' on type 'VolumeHeader'. Did you mean 'number'? (line 1, column 13):\n{ volumes { number1 } }\n            ^","locations":[{"line":1,"column":13}]}]}```
```

If the query is malformed, you may get something like:

```
{
  "errors": [
    {
      "message": "Error processing GraphQL query",
      "type": "GraphQlError",
      "cause": "body cannot be converted to GraphQlQuery: Unable to decode JSON payload: Syntax error while parsing GraphQL query. Invalid input \"{\\n  volumes {\\n    number\\n    title\\n    work {\\n      \\n    }\", expected Comments or SelectionSet (line 1, column 1):\n{\n^."
    }
  ]
}
```

In the case of multiple errors your response may look like:

```
{
  "errors": [
    {
      "message": "Cannot query field 'extraConfig' on type 'ArtPrintConfig'. Did you mean 'addnConfig'? (line 14, column 9):\n        extraConfig {\n        ^",
      "locations": [
        {
          "line": 14,
          "column": 9
        }
      ]
    },
    {
      "message": "Cannot query field 'allConfig' on type 'DeviceCaseConfig'. Did you mean 'addnConfig'? (line 44, column 9):\n        allConfig {\n        ^",
      "locations": [
        {
          "line": 44,
          "column": 9
        }
      ]
    }
  ]
}
```

# Environments

## Local Development

* `http://localhost:8080`

## Production

* `https://rb-graphql-template.herokuapp.com`

Substitute the domain `rb-graphql-template` in the examples below with this.

# Operations

All operations are performed using GraphQL. The GraphQL endpoint accepts both `GET` and `POST` requests, though `GET`s
are limited in the length of the query string and may fail (with 400s). Where possible, prefer `POST`s.

* **URL pattern:** `http://rb-graphql-template/v1/graphql`

* **HTTP Verb:** `POST`

## Example Request/Response

### Success

```
$ curl -i -X POST --data '{ "query" : "{ volumes { number } }" }' "http://localhost:8080/v1/graphql"
HTTP/1.1 200 OK
Content-Type: application/json
Content-Length: 35

{"data":{"volumes":[{"number":1}]}}
```

### Failure

```
$ curl -i -X POST --data '{ "query" : "{ volumes { number1 } }" }' "http://localhost:8080/v1/graphql"
HTTP/1.1 400 Bad Request
Content-Type: application/json
Content-Length: 200

{"errors":[{"message":"Cannot query field 'number1' on type 'VolumeHeader'. Did you mean 'number'? (line 1, column 13):\n{ volumes { number1 } }\n            ^","locations":[{"line":1,"column":13}]}]}
```
