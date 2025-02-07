import scala.collection.Seq

ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.5"

lazy val root = (project in file("."))
  .settings(
    name := "untitled",
    libraryDependencies ++= Seq(
      "com.github.tototoshi" %% "scala-csv" % "2.0.0",
      "org.tpolecat" %% "doobie-core" % "1.0.0-RC5",
      "org.tpolecat" %% "doobie-hikari" % "1.0.0-RC5",
      "org.playframework" %% "play-json" % "3.0.4", //librería para procesar cadenas de texto en formato JSON a  valores JsValue
      "com.mysql" % "mysql-connector-j" % "9.1.0"
  )
  )