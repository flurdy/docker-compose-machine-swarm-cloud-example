name := """service"""

version := "1.0"

scalaVersion := "2.11.5"

scalacOptions ++= Seq("-Xmax-classfile-name", "130") 

scalacOptions in Compile ++= Seq("-Xmax-classfile-name", "130")

libraryDependencies ++= {
  val akkaV = "2.3.9"
  val sprayV = "1.3.2"
  val slickV = "2.1.0"
  Seq(
    "io.spray"            %%  "spray-can"     % sprayV,
    "io.spray"            %%  "spray-http"    % sprayV,
    "io.spray"            %%  "spray-routing" % sprayV,
    "io.spray"            %%  "spray-json"    % "1.3.1",
    "io.spray"            %%  "spray-testkit" % sprayV  % "test",
    "com.typesafe.akka"   %%  "akka-actor"    % akkaV,
    "com.typesafe.akka"   %%  "akka-testkit"  % akkaV   % "test",
    "org.slf4j"           %   "slf4j-api"     % "1.7.7",
    "com.github.tototoshi" %% "slick-joda-mapper" % "1.2.0",
    "com.typesafe.slick"  %%  "slick"         % slickV,
    "com.typesafe.slick"  %%  "slick-testkit" % slickV % "test",
    "org.postgresql"      %   "postgresql"    % "9.3-1102-jdbc4",
    "com.h2database"      %   "h2"            % "1.3.175",
    "com.jolbox"          %   "bonecp"        % "0.8.0.RELEASE",
    "ch.qos.logback"      %   "logback-classic" % "1.1.1",
    "commons-daemon"      %   "commons-daemon" % "1.0.15"
  )
}

assemblyJarName in assembly := "pizzaservice.jar"

// mainClass in assembly := Some("com.flurdy.example.server.ServiceApplication")

// assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = true, includeDependency = false)

assemblyExcludedJars in assembly := { 
  val cp = (fullClasspath in assembly).value
  cp filter {_.data.getName == "shapeless_2.11-1.2.4.jar"}
}

parallelExecution in Test := false

logBuffered := false

Revolver.settings
