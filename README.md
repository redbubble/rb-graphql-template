[![Build status](https://img.shields.io/travis/redbubble/rb-graphql-template/master.svg)](https://travis-ci.org/redbubble/rb-graphql-template)

# GraphQL Template API

This is a service template for a GraphQL-based API. It is a small stateless HTTP API that aggregates, proxies and transforms downstream APIs. In particular, this template exposes a subset of the [Star Wars API](https://swapi.co).

It aims to provide a simple, consistent, beginner- to intermediate-level stack, aimed at getting a small HTTP-based service up & running quickly with some things we care about in a production system, including:

* A GraphQL API exposed over HTTP;
* Authentication;
* Caching;
* System monitoring;
* Error reporting;
* Metrics;
* Logging;
* Testing;
* Deployment.

This template uses other open source code from Redbubble:

* [rb-scala-utils](https://github.com/redbubble/rb-scala-utils) - Miscellaneous utilities (common code) for building
  Scala-based services, using Finch (on which this project depends).
* [finch-sangria](https://github.com/redbubble/finch-sangria) - A simple wrapper for using Sangria from within Finch;
* [finagle-hawk](https://github.com/redbubble/finagle-hawk) - HTTP Holder-Of-Key Authentication Scheme for Finagle.

Redbubble also makes available a purely [HTTP version of this template](https://github.com/redbubble/finch-template) (on which this is based).

## Architecture

The architecture of the app is essentially composed vertically, representing a request's flow through the system. Each layer basically only talks to its adjacent layers.

For simplicity of the template (i.e. you may not really do this), the top-level packages are grouped into their functional areas, and are as follows:

* `api` - The HTTP API that we expose to clients. Decodes incoming JSON GraphQL queries & sends them to be executed. Also handles caching at a query-level.
* `graphql` - The data that is exposed, via GraphQL to a client. Basically these should only do marshalling to & from GraphQL. As these are exposed to clients, we call the classes that make up this layer `API`s.
  to a service.
* `services` - High level business logic; compose fetches to produce a result & also run the fetches. Usually expose a cleaner API than the underlying backend or fetcher.
* `fetch` - Understand how to fetch data from backend datasources. Includes lower level caching of fetched data.
* `backends` - Clients for talking to backend or downstream services. Expose an API that directly mirrors the backend they talk to.
* `util` - Any other code that doesn't fall neatly into one of the above functional buckets.

# API

The API uses GraphQL. GraphiQL, an interactive browser, is available locally (though not in production): http://localhost:8080/v1/explore

There is also simple [API documentation](./app/API.md) available. Where possible, prefer the GraphiQL online documentation as it will be up to date.

# Setup

To setup for local development, run these steps. Note that most steps assume that you've changed directory to `app`.

1. Install [Java 1.8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) from Oracle.
   You will need a JDK (not a JRE), as of the time of writing this is "Java SE Development Kit 8u92". There is also
   [documentation](http://www.oracle.com/technetwork/java/javase/documentation/jdk8-doc-downloads-2133158.html)
   available (handy for linking into your IDE).

1. Run [sbt](http://www.scala-sbt.org/release/docs/Getting-Started/Setup.html):

    ```
    $ cd app
    $ ./sbt
    > update
    ```

    Note. You could also `brew install sbt` if you'd prefer a system version.

1. `etc/development-env` contains a template `.env` file that you can use to configure your environment variables locally:

    ```
    $ cd app
    $ cp etc/development.env .env
    ```

  See [sbt-dotenv](https://github.com/mefellows/sbt-dotenv) for more information. *Do not check `.env` into source control!*.

  Note that if you change this while an sbt session is running, you will need to `reload` sbt for the new settings to
  take effect (as it's an sbt plugin).

1. If you're using IntelliJ for development, grab the Scala plugin, and import the project (File->Open, or Import from
   the welcome screen) as an SBT project. If you want to run tests, you will need to add the environment variables (in
   `.env`) to the run configurations (specs2).

# Running

## Locally

To run using sbt:

```
$ ./sbt run
```

You can also use [Revolver](https://github.com/spray/sbt-revolver) for restarts when code changes (recommended!):

```
$ ./sbt ~re-start
```

To run using the Heroku tools (requires deployment setup as above), i.e. like it's run in production:

```
$ ./sbt compile stage
$ heroku local        # which uses [forego](https://github.com/ddollar/forego)
```

To run in debug mode:

```
$ ./sbt run -jvm-debug 5005
```

You can then connect a remote debugger to the JVM.

## Production

Deploying to production will restart the app servers (Heroku Dynos) automatically. However if you wish to restart
manually you can:

```
$ heroku restart -a rb-ios-api
```

# Testing

```
$ ./sbt test
```

This will start the `sbt` REPL, from where you can issue [commands](http://www.scala-sbt.org/0.13/docs/Running.html#Common+commands).

* `test` - Runs all tests;
* `test-only com.redbubble.gql.core.CurrencySpec` - Runs a single test;
* `test-only com.redbubble.gql.core.*` - Runs all tests in the `com.redbubble.gql.core` package;
* `test-only *CurrencySpec` - Runs `CurrencySpec`;
* `test:compile` - Compiles your test code.

Appending a `~` to the start of any sbt command will run it continuously; for example to run tests continuously:

```
> ~test
```

# Performance Testing

*Note. There are incompatibilities with Gatling & the Netty that Finagle uses at present, the performance tests may not work. If you want to use them, the recommendation is to move back down to Scala 2.11 (for all deps) and use that Gatling version.*

Performance testing uses [Gattling](http://gatling.io/), and live in the `perf/src/it` directory. You can run
all performance tests using the following:

```
> gatling-it:test
```

Or individual scenarios as:

```
> gatling-it:test-only com.redbubble.perf.scenarios.AppStartup
> gatling-it:test-only *AppStartup
```

For more information see: http://gatling.io/docs/2.2.4/extensions/sbt_plugin.html

Note. If you run tests against a local server you will need to start it first.

# Deployment

Deployment is to Heroku. There is a Docker container for testing & runtime, that could be repurposed for deployment.

Deployment to Heroku can be done using:

```
$ ./deploy
```

Before you do though, there is a one-off setup for deployment.

1. Get an account on Heroku.

1. Install [heroku toolbelt](https://toolbelt.heroku.com/).

1. Add the git remote (this is a one-off step).

    ```
    $ heroku git:remote -a graphql-template-api
    ```

The deployment uses the [SBT Native Packager](https://github.com/sbt/sbt-native-packager) to package up the artifacts
for deployment. You can run this locally using the `stage` command:

```
$ ./sbt compile stage
```
Here is more information on deployment:

* [Scala on Heroku](https://devcenter.heroku.com/articles/deploying-scala)
* [Scala Buildpack](https://github.com/heroku/heroku-buildpack-scala)
* [An sbt plugin for deploying Heroku Scala applications](https://github.com/heroku/sbt-heroku)

# Build

Builds are done using [Buildkite](https://buildkite.com/) and run under Docker. See the [Makefile](./Makefile) for details.

To simulate locally what Buildkite runs (`test.sh`), you can run the following:

```
$ make test
```

# Logs

## Local

All logs go to standard out locally when developing.

## Production

You can get the logs using:

```
$ heroku logs -t -a graphql-template
```

This will only store the last 1500 lines, if you want to view more you can enable the PaperTrail plugin.

# Monitoring

The system is monitored via New Relic. Note that via Heroku we don't get system level metrics such as CPU & memory.

# Metrics

Metrics are exposed via [Twitter Metrics](https://twitter.github.io/finagle/guide/Metrics.html). See
`com.redbubble.gql.util.metrics.Metrics` for the entry point.

## Local

Metrics are available locally on the admin server: http://localhost:9990/admin

## Production

Metrics into production are bridged to New Relic (sent every minute) as custom metrics and are available on the New
Relic insights dashboard.

Note that when sending to New Relic, we do filter out some metrics that are collected locally; in particular the JVM
metrics (as NR collects these already), as well as tools we don't use (e.g. Zipkin). We also only send the 75th, 95th,
99th & 99.9th percentiles, along with 1, 5 & 15 minute weighted moving averages (for the appropriate metrics type where
supported).

# Development Overview

This section is aimed at developers on the project, and gives a quick overview of the features & lbraries used:

* HTTP stack, using [Finch](https://github.com/finagle/finch);
* Authentication using [Hawk](https://github.com/hueniverse/hawk), a HMAC-style protocol;
* JSON encoding & decoding, using [Circe](https://github.com/circe/circe), including reasonable error handling;
* Caching, batching & fetching using [Fetch](http://47deg.github.io/fetch/docs);
* Downstream service clients using [Featherbed](https://finagle.github.io/featherbed);
* [Metrics](https://twitter.github.io/finagle/guide/Metrics.html), logged to New Relic;
* System monitoring via New Relic;
* Error reporting to [Rollbar](https://rollbar.com)
* Testing using [specs2](https://etorreborre.github.io/specs2/) & [ScalaCheck](https://www.scalacheck.org);
* Logging to stdout;
* Deployment packaging using [SBT Native Packager](https://github.com/sbt/sbt-native-packager).

## Tools/Frameworks/Libraries

### Finch

* [Finch best practices](https://github.com/finagle/finch/blob/master/docs/best-practices.md)
* [Finagle 101](http://vkostyukov.net/posts/finagle-101/)
* [Finch 101](http://vkostyukov.ru/slides/finch-101/)

### Finagle

* [Getting started with Finagle](http://andrew-jones.com/blog/getting-started-with-finagle/)
* [An introduction to Finagle](http://twitter.github.io/scala_school/finagle.html)
* [Finagle examples](https://www.codatlas.com/github.com/twitter/finagle/develop)
* [Other information on Finagle](http://dirtysalt.github.io/finagle.html)

### Cats

* [Cats documentation](http://typelevel.org/cats/)
* [Herding Cats](http://eed3si9n.com/herding-cats/) - an introduction/tutorial on Cats

### GraphQL

* [Learn GrqphQL](https://learngraphql.com)
* [GrqphQL Spec](https://facebook.github.io/graphql)
* [GraphQL Queries](http://graphql.org/docs/queries/)

### Sangria

* [Learn Sangira](http://sangria-graphql.org/learn/)

## Uninstall

You can uninstall everything you installed for this project by:

```
$ rm -rf ~/.sbt
$ rm -rf ~/.ivy2
```

Then, if you want, you can uninstall Java by following the instructions here: https://docs.oracle.com/javase/8/docs/technotes/guides/install/mac_jdk.html#A1096903
