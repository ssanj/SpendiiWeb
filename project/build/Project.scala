import sbt._

class Project(info: ProjectInfo) extends DefaultWebProject(info) {

  lazy val artifactory = "Artifactory Release" at "http://hyperion:9080/artifactory/libs-releases"
  lazy val liftVersion = "2.1-RC2"

  lazy val mongoDriver = "org.mongodb" % "mongo-java-driver" % "2.1" withSources()
  
  override def libraryDependencies = Set(
    "net.liftweb" %% "lift-webkit" % liftVersion % "compile->default",
    "net.liftweb" %% "lift-common" % liftVersion % "compile->default",
    "net.liftweb" %% "lift-mapper" % liftVersion % "compile->default",
    "org.mortbay.jetty" % "jetty" % "6.1.22" % "test->default"
    ) ++ super.libraryDependencies
}


