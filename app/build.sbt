import com.gilt.sbt.newrelic.NewRelic.autoImport._
import com.typesafe.sbt.packager.archetypes.JavaAppPackaging
import sbt.{ExclusionRule, Resolver}
//import JavaAppPackaging.autoImport._
import sbt.Keys._
//import scoverage._

lazy val buildSettings = Seq(
  organization := "com.redbubble",
  scalaVersion := "2.12.3"
)

lazy val finagleHawkVersion = "0.2.1"
lazy val finchSangriaVersion = "0.3.1"
lazy val rbScalaUtilsVersion = "0.1.1"
lazy val circeVersion = "0.8.0"
lazy val catsVersion = "0.9.0"
lazy val mouseVersion = "0.9"
// The version numbers for Finagle, Twitter, Finch & Catbird *must* work together. See the Finch build.sbt for known good versions.
lazy val finagleVersion = "6.45.0"
lazy val finagleHttpAuthVersion = "0.1.0"
lazy val twitterServerVersion = "1.30.0"
lazy val finchVersion = "0.15.1"
lazy val sangriaVersion = "1.2.2"
lazy val sangriaCirceVersion = "1.1.0"
lazy val featherbedVersion = "0.3.1"
lazy val specsVersion = "3.9.4"
lazy val scalaCacheVersion = "0.9.4"
lazy val scalaUriVersion = "0.4.16"
lazy val fetchVersion = "0.6.2"
lazy val slf4jVersion = "1.7.25"
lazy val gattlingVersion = "2.2.5"
lazy val nrVersion = "3.40.0"

lazy val finagleHawk = "com.redbubble" %% "finagle-hawk" % finagleHawkVersion
lazy val finchSangria = "com.redbubble" %% "finch-sangria" % finchSangriaVersion
lazy val rbScalaUtils = "com.redbubble" %% "rb-scala-utils" % rbScalaUtilsVersion
lazy val cats = "org.typelevel" %% "cats-core" % catsVersion
lazy val circeCore = "io.circe" %% "circe-core" % circeVersion
lazy val mouse = "com.github.benhutchison" %% "mouse" % mouseVersion
lazy val finagleHttp = "com.twitter" %% "finagle-http" % finagleVersion
lazy val finagleStats = "com.twitter" %% "finagle-stats" % finagleVersion
lazy val finagleHttpAuth = "com.github.finagle" %% "finagle-http-auth" % finagleHttpAuthVersion
lazy val twitterServer = "com.twitter" %% "twitter-server" % twitterServerVersion
lazy val finchCore = "com.github.finagle" %% "finch-core" % finchVersion
lazy val finchCirce = "com.github.finagle" %% "finch-circe" % finchVersion
lazy val scalaUri = "com.netaporter" %% "scala-uri" % scalaUriVersion
lazy val fetch = "com.47deg" %% "fetch" % fetchVersion
lazy val featherbedCore = "io.github.finagle" %% "featherbed-core" % featherbedVersion
lazy val featherbedCirce = "io.github.finagle" %% "featherbed-circe" % featherbedVersion
lazy val sangria = "org.sangria-graphql" %% "sangria" % sangriaVersion
lazy val sangriaCirce = "org.sangria-graphql" %% "sangria-circe" % sangriaCirceVersion
lazy val sangriaRelay = "org.sangria-graphql" %% "sangria-relay" % sangriaVersion
lazy val newRelic = "com.newrelic.agent.java" % "newrelic-api" % nrVersion
lazy val gattlingHighCharts = ("io.gatling.highcharts" % "gatling-charts-highcharts" % gattlingVersion % "it,test")
    .excludeAll(ExclusionRule("org.scala-lang.modules", "scala-xml_2.11"))
    .excludeAll(ExclusionRule("org.scala-lang.modules", "scala-java8-compat_2.11"))
    .excludeAll(ExclusionRule("org.scala-lang.modules", "scala-parser-combinators_2.11"))
lazy val gattlingTest = ("io.gatling" % "gatling-test-framework" % gattlingVersion % "it,test")
    .excludeAll(ExclusionRule("org.scala-lang.modules", "scala-xml_2.11"))
    .excludeAll(ExclusionRule("org.scala-lang.modules", "scala-java8-compat_2.11"))
    .excludeAll(ExclusionRule("org.scala-lang.modules", "scala-parser-combinators_2.11"))
lazy val specsCore = "org.specs2" %% "specs2-core" % specsVersion
lazy val specsScalacheck = "org.specs2" %% "specs2-scalacheck" % specsVersion

// https://tpolecat.github.io/2014/04/11/scalac-flags.html
lazy val compilerOptions = Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-feature",
  "-language:existentials",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-language:reflectiveCalls",
  "-unchecked",
  "-Yno-adapted-args",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Xfuture",
  "-Xlint",
  //"-Yno-predef",
  //"-Ywarn-unused-import", // gives false positives
  "-Xfatal-warnings",
  "-Ywarn-value-discard",
  "-Ypartial-unification"
)

lazy val apiDependencies = Seq(
  finagleHawk,
  finchSangria,
  rbScalaUtils,
  cats,
  mouse,
  circeCore,
  twitterServer,
  finagleHttp,
  finagleHttpAuth,
  finagleStats,
  finchCore,
  finchCirce,
  sangria,
  sangriaCirce,
  sangriaRelay,
  scalaUri,
  fetch,
  featherbedCore,
  featherbedCirce,
  newRelic
)

lazy val perfDependencies = Seq(
  gattlingHighCharts,
  gattlingTest
)

lazy val testDependencies = Seq(
  specsCore,
  specsScalacheck
)

lazy val baseSettings = Seq(
  libraryDependencies ++= testDependencies.map(_ % "test"),
  resolvers ++= Seq(
    Resolver.jcenterRepo,
    Resolver.sonatypeRepo("releases"),
    Resolver.sonatypeRepo("snapshots"),
    "Twitter" at "http://maven.twttr.com",
    Resolver.url("bintray-sbt-plugin-releases", url("http://dl.bintray.com/content/sbt/sbt-plugin-releases"))(Resolver.ivyStylePatterns),
    Resolver.bintrayRepo("redbubble", "open-source")
  ),
  scalacOptions ++= compilerOptions,
  scalacOptions in Test ++= Seq("-Yrangepos"),
  scalacOptions in(Compile, console) += "-Yrepl-class-based",
  testOptions in Test += Tests.Setup(() => System.setProperty("ENV", "test"))
)

lazy val apiSettings = buildSettings ++ baseSettings ++ Seq(
  libraryDependencies ++= apiDependencies
)

lazy val perfSettings = buildSettings ++ baseSettings ++ Defaults.itSettings ++ Seq(
  libraryDependencies ++= perfDependencies
)

lazy val graphqlTemplateSettings = buildSettings ++ baseSettings ++ Seq(
  name := "rb-graphql-template",
  moduleName := "rb-graphql-template",
  libraryDependencies ++= apiDependencies,
  mainClass in Compile := Some("com.redbubble.gql.App"),
  aggregate in run := false,
  aggregate in reStart := false,
  coverageMinimum := 25.0,
  coverageFailOnMinimum := true,
  coverageExcludedPackages := "com\\.redbubble\\.graphql\\.util\\.spec\\.*",
  newrelicAppName := "GraphQL Template",
  newrelicVersion := nrVersion,
  newrelicCustomTracing := true,
  newrelicIncludeApi := true
)

lazy val api = (project in file("api"))
    .settings(apiSettings)
lazy val perf = (project in file("perf"))
    .settings(perfSettings)
    .configs(IntegrationTest)
    .dependsOn(api % "test->test;compile->compile")
    .enablePlugins(GatlingPlugin)
lazy val graphqlTemplate = project.in(file("."))
    .settings(graphqlTemplateSettings)
    .aggregate(api, perf)
    .dependsOn(api, perf)
    .enablePlugins(JavaAppPackaging, NewRelic)

shellPrompt in ThisBuild := { state =>
  s"${scala.Console.MAGENTA}${Project.extract(state).currentRef.project}> ${scala.Console.RESET}"
}

// Note. We aggregate coverage as a separate command in CI, see https://github.com/scoverage/sbt-scoverage#multi-project-reports
val ciCommands = List(
  "clean",
  // TODO Re-enable this.
  //  "scalastyle",
  //  "test:scalastyle",
  "compile",
  "test:compile",
  "coverage",
  "test",
  "coverageReport",
  "stage"
)
addCommandAlias("ci", ciCommands.mkString(";", ";", ""))
