lazy val commonSettings = Seq(
  version := "1.0",
  scalaVersion := "2.11.8",
  resolvers += Resolver.sonatypeRepo("snapshots"),
  resolvers += Resolver.sonatypeRepo("releases"),
  addCompilerPlugin("org.scalamacros" % "paradise_2.11.8" % "3.0.0-M3")
)

lazy val core = (project in file("core"))
  .settings(commonSettings: _*)
  .dependsOn(macros)

lazy val macros = (project in file("macro"))
  .settings(commonSettings: _*)
  .settings(
    libraryDependencies ++= Seq(
      "org.scalameta" %% "scalameta" % "1.0.0"
    )
  )
