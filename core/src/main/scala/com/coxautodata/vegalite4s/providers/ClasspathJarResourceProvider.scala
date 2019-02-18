package com.coxautodata.vegalite4s.providers

import java.io.InputStream
import java.net.URLClassLoader
import java.nio.file.{Files, Paths}
import java.util.jar.{JarEntry, JarFile}

import scala.collection.JavaConverters._
import scala.collection.immutable.Stream
import scala.collection.immutable.Stream.cons

/**
  * A VegaLite provider that find the Vega, Vega-Lite and Vega-Embed
  * Javascript libraries by searching in the Jar dependencies on the
  * classpath and embeds them in the rendered plot using the
  * [[InputStreamProvider]].
  * This allows all plot Javascript dependencies to be provided by
  * Maven artifacts, i.e. by setting the following as a project dependency:
  * ```
  * "org.webjars.npm" % "vega" % "3.3.1" % intransitive ()
  * "org.webjars.npm" % "vega-lite" % "2.6.0" % intransitive ()
  * "org.webjars.npm" % "vega-embed" % "3.29.1" % intransitive ()
  * ```
  */
object ClasspathJarResourceProvider {

  def apply(vegaLiteSchemaVersion: String): VegaLiteProvider = {
    val streams = findResourcesByFilename(
      List("vega.min.js", "vega-lite.min.js", "vega-embed.min.js")
    )

    streams
      .collect { case Left(f) => f }
      .reduceOption(_ + ", " + _)
      .foreach(
        f =>
          throw new RuntimeException(
            s"Failed to find the following files in classpath JARs: [$f]"
        )
      )

    new InputStreamProvider(vegaLiteSchemaVersion, streams.collect {
      case Right((_, in)) => in
    }: _*)

  }

  type MaybeFileFound = Either[String, (String, InputStream)]

  private def matchJarEntry(
      jar: JarFile
  )(toFind: List[MaybeFileFound], jarEntry: JarEntry): List[MaybeFileFound] = {
    val (matched, notMatched) =
      toFind.partition(e => e.left.exists(jarEntry.getName.split("/").last ==))
    matched.headOption
      .map(v => List(Right((v.left.get, jar.getInputStream(jarEntry)))))
      .getOrElse(List.empty) ++ notMatched
  }

  private def findResourcesInJar(toFind: List[MaybeFileFound],
                                 jar: JarFile): List[MaybeFileFound] =
    jar
      .entries()
      .asScala
      .toStream
      .scanLeft(toFind)(matchJarEntry(jar))
      .takeUntil(_.exists(_.isLeft))
      .lastOption
      .getOrElse(toFind)

  /**
    * Find the named resources in JARs on the classpath and
    * return an InputStream for each.
    * Finishes early when all are found. Results are returned in the order
    * they were given.
    */
  private def findResourcesByFilename(
      files: List[String]
  ): List[MaybeFileFound] = {
    val input: List[MaybeFileFound] = files.map(Left(_))
    ClassLoader.getSystemClassLoader
      .asInstanceOf[URLClassLoader]
      .getURLs
      .toStream
      .filter(_.getProtocol == "file")
      .map(_.toURI)
      .map(Paths.get)
      .filter(
        p => Files.isRegularFile(p) && p.toString.toLowerCase.endsWith("jar")
      )
      .map(p => new JarFile(p.toFile))
      .scanLeft(input)(findResourcesInJar)
      .takeUntil(_.exists(_.isLeft))
      .lastOption
      .getOrElse(input)
      .sortWith(orderByInput(files))
  }

  private def orderByInput(input: List[String])(a: MaybeFileFound,
                                                b: MaybeFileFound): Boolean = {
    input.indexOf(getFilename(a)) < input.indexOf(getFilename(b))
  }

  private def getFilename(f: MaybeFileFound): String = f match {
    case Left(n)       => n
    case Right((n, _)) => n
  }

  private implicit class StreamExtension[A](s: Stream[A]) {

    /**
      * Like [[Stream.takeWhile]] but inclusive of the element that matches
      * the predicate
      */
    def takeUntil(p: A => Boolean): Stream[A] =
      if (s.nonEmpty && p(s.head)) cons(s.head, s.tail takeUntil p)
      else if (s.nonEmpty) cons(s.head, Stream.Empty)
      else Stream.Empty

  }

}
