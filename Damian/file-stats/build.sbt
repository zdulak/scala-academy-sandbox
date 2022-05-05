ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.8"

lazy val root = (project in file("."))
  .settings(
    name := "file-stats"
  )

libraryDependencies += "com.github.scopt" %% "scopt" % "4.0.1"

enablePlugins(DockerPlugin)

docker / dockerfile := {
  val jarFile: File = (Compile / packageBin / sbt.Keys.`package`).value
  val classpath = (Compile / managedClasspath).value
  val mainclass = (Compile / packageBin / mainClass).value.getOrElse(sys.error("Expected exactly one main class"))
  val libs = "/app/libs"
  val jarTarget = s"/app/${jarFile.getName}"
  // Make a colon separated classpath with the JAR file
  val classpathString = classpath.files.map("/app/" + _.getName)
    .mkString(":") + ":" + jarTarget
  new Dockerfile {
    // Base image
    from("openjdk:11-jre")
    // Copy all dependencies to 'libs' in the staging directory
    classpath.files.foreach { depFile =>
      val target = file(libs) / depFile.name
      stageFile(depFile, target)
    }
    // Add the libs dir from the
    addRaw(libs, libs)
    // Add all files on the classpath
    add(classpath.files, "/app/")
    // Add the JAR file
    add(jarFile, jarTarget)
    // The classpath is the 'libs' dir and the produced jar file
    val classpathString = s"$libs/*:$jarTarget"
    // On launch run Java with the classpath and the main class
    entryPoint("java", "-cp", classpathString, mainclass)
  }
}
