val projectScalaVersion = "2.11.12"

name := "vegalite4s"

import sbt.Keys.{developers, fork, homepage, scalaVersion, scmInfo}
import sbt.url
import xerial.sbt.Sonatype._

// PROJECTS

lazy val global = project
  .in(file("."))
  .settings(
    settings ++ Seq(
      name := "vegalite4s-parent",
      skip in publish := true,
      publish := {},
      publishLocal := {},
      publishArtifact := false
    )
  )
  .aggregate(core, macros, spark2)

lazy val core = project
  .settings(
    name := "vegalite4s",
    settings,
    libraryDependencies ++= commonDependencies ++ coreDependencies,
    mappings in (Compile, packageBin) ++= mappings
      .in(macros, Compile, packageBin)
      .value,
    mappings in (Compile, packageSrc) ++= mappings
      .in(macros, Compile, packageSrc)
      .value
  )
  .dependsOn(macros % "compile-internal, test-internal")

lazy val macros = project
  .settings(
    name := "vegalite4s-macros",
    settings,
    libraryDependencies ++= commonDependencies
  )

lazy val spark2 = project
  .settings(
    name := "vegalite4s-spark2",
    settings,
    libraryDependencies ++= commonDependencies ++ sparkDependencies
  )
  .dependsOn(core)

// DEPENDENCIES

lazy val dependencies =
  new {
    val circeV = "0.11.1"
    val scalafxV = "8.0.144-R12"
    val scalatestV = "3.0.4"
    val spark2V = "2.0.0"
    val vegaliteV = "2.6.0"
    val vegaV = "3.3.1"
    val vegaembedV = "3.29.1"
    val scalaloggingV = "3.9.0"
    val slf4jV = "1.7.16"

    val scalareflect = "org.scala-lang" % "scala-reflect" % projectScalaVersion
    val vegalite = "org.webjars.npm" % "vega-lite" % vegaliteV
    val vega = "org.webjars.npm" % "vega" % vegaV
    val vegaembed = "org.webjars.npm" % "vega-embed" % vegaembedV
    val scalafx = "org.scalafx" %% "scalafx" % scalafxV
    val scalalogging = "com.typesafe.scala-logging" %% "scala-logging" % scalaloggingV
    val slf4j = "org.slf4j" % "slf4j-simple" % slf4jV
    val circecore = "io.circe" %% "circe-core" % circeV
    val circegeneric = "io.circe" %% "circe-generic" % circeV
    val circeparser = "io.circe" %% "circe-parser" % circeV
    val spark2 = "org.apache.spark" %% "spark-sql" % spark2V

    val scalatest = "org.scalatest" %% "scalatest" % scalatestV
  }

lazy val commonDependencies = Seq(
  dependencies.circecore % "compile",
  dependencies.circegeneric % "compile",
  dependencies.circeparser % "compile",
  dependencies.scalareflect % "compile",
  dependencies.scalafx % "compile",
  dependencies.scalalogging % "compile",
  dependencies.slf4j % "compile",
  dependencies.scalatest % "test"
)

lazy val coreDependencies = Seq(
  dependencies.vegalite % "test" intransitive (),
  dependencies.vega % "test" intransitive (),
  dependencies.vegaembed % "test" intransitive ()
)

lazy val sparkDependencies = Seq(dependencies.spark2 % "optional")

// SETTINGS
lazy val settings =
  commonSettings ++
    scalafmtSettings

lazy val compilerOptions = Seq(
  "-unchecked",
  "-feature",
  "-language:existentials",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-language:postfixOps",
  "-deprecation",
  "-target:jvm-1.8",
  "-encoding",
  "utf8"
)

lazy val commonSettings = Seq(
  organization in ThisBuild := "com.coxautodata",
  scalaVersion := projectScalaVersion,
  crossScalaVersions in ThisBuild := Seq("2.11.12", "2.12.8"),
  fork in Test := true,
  publishArtifact in Test := true,
  publishConfiguration := publishConfiguration.value
    .withOverwrite(isSnapshot.value),
  publishLocalConfiguration := publishLocalConfiguration.value
    .withOverwrite(isSnapshot.value),
  licenses := Seq(
    "APL2" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt")
  ),
  description := "A light-weight library for generating VegaLite visualisations in Scala and Spark",
  scalacOptions ++= compilerOptions,
  fork in Test := true,
  resolvers ++= Seq(
    "Local Maven Repository" at "file://" + Path.userHome.absolutePath + "/.m2/repository",
    Resolver.sonatypeRepo("releases"),
    Resolver.sonatypeRepo("snapshots")
  ),
  sonatypeProjectHosting := Some(
    GitHubHosting(
      "CoxAutomotiveDataSolutions",
      "vegalite4s",
      "alex.bush@coxauto.co.uk"
    )
  ),
  homepage := Some(
    url("https://github.com/CoxAutomotiveDataSolutions/vegalite4s")
  ),
  scmInfo := Some(
    ScmInfo(
      url("https://github.com/CoxAutomotiveDataSolutions/vegalite4s"),
      "scm:git@github.com:CoxAutomotiveDataSolutions/vegalite4s.git"
    )
  ),
  developers := List(
    Developer(
      id = "alexjbush",
      name = "Alex Bush",
      email = "alex.bush@coxauto.co.uk",
      url = url("https://alexbu.sh")
    )
  )
)

lazy val scalafmtSettings =
  Seq(
    scalafmtOnCompile := true,
    scalafmtTestOnCompile := true,
    scalafmtVersion := "1.2.0"
  )
