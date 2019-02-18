val projectScalaVersion = "2.11.12"

name := "vegalite4s"

import sbt.Keys.{developers, fork, homepage, scalaVersion, scmInfo}
import sbt.url
import xerial.sbt.Sonatype._

val circeV = "0.11.1"
val scalafxV = "8.0.144-R12"
val scalatestV = "3.0.4"
val spark2V = "2.4.0"
val vegaliteV = "2.6.0"
val vegaV = "3.3.1"
val vegaembedV = "3.29.1"
val scalaloggingV = "3.9.0"
val slf4jV = "1.7.16"
val sparkV = "2.4.0"

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
    libraryDependencies ++= Seq(
      "org.scala-lang" % "scala-reflect" % scalaVersion.value % "compile",
      "org.scalafx" %% "scalafx" % scalafxV % "compile",
      "com.typesafe.scala-logging" %% "scala-logging" % scalaloggingV % "compile",
      "org.slf4j" % "slf4j-simple" % slf4jV % "compile",
      "io.circe" %% "circe-core" % circeV % "compile",
      "io.circe" %% "circe-generic" % circeV % "compile",
      "io.circe" %% "circe-parser" % circeV % "compile",
      "org.scalatest" %% "scalatest" % scalatestV % "test",
      "org.webjars.npm" % "vega-lite" % vegaliteV % "test" intransitive (),
      "org.webjars.npm" % "vega" % vegaV % "test" intransitive (),
      "org.webjars.npm" % "vega-embed" % vegaembedV % "test" intransitive ()
    )
  )
  .dependsOn(macros)

lazy val macros = project
  .settings(
    name := "vegalite4s-macros",
    settings,
    libraryDependencies ++= Seq(
      "org.scala-lang" % "scala-reflect" % scalaVersion.value % "compile")
  )

lazy val spark2 = project
  .settings(name := "vegalite4s-spark2",
            settings,
            libraryDependencies ++= Seq(
              "org.apache.spark" %% "spark-sql" % sparkV % "optional",
              "org.scalatest" %% "scalatest" % scalatestV % "test"
            ))
  .dependsOn(core)

// SETTINGS
lazy val settings =
  commonSettings

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
  crossScalaVersions := Seq("2.11.12", "2.12.8"),
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