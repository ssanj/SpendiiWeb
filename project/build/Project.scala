import _root_.shortbread.ShortbreadPlugin
import _root_.shortbread.DefaultDrivers._
import sbt._


class Project(info: ProjectInfo) extends DefaultWebProject(info) with ShortbreadPlugin {

  import _root_.shortbread.NamedDriver

  lazy val artifactory = "Artifactory Release" at "http://hyperion:9080/artifactory/libs-releases"
  lazy val liftVersion = "2.2-M1"

  lazy val mongoDriver = "org.mongodb" % "mongo-java-driver" % "2.1" withSources()

  override def libraryDependencies = Set(
    "net.liftweb" %% "lift-webkit" % liftVersion % "compile->default" withSources(),
    "net.liftweb" %% "lift-common" % liftVersion % "compile->default" withSources(),
    "net.liftweb" %% "lift-mapper" % liftVersion % "compile->default" withSources(),
    "net.liftweb" %% "lift-util" % liftVersion % "compile->default" withSources(),
    "org.slf4j" % "slf4j-simple" % "1.6.1" % "compile->default",
    "org.scalatest" % "scalatest" % "1.2" % "test->default" withSources(),
    "org.mortbay.jetty" % "jetty" % "6.1.22" % "test->default"
    ) ++ super.libraryDependencies

  override def compileOptions = CompileOption("-encoding") :: CompileOption("UTF-8") :: CompileOption("-unchecked") :: super.compileOptions.toList

  override def driverSeq:Seq[NamedDriver] = Seq(DefaultFoxConfig.webDriver)

  override def exitOnCompletion = false
}


