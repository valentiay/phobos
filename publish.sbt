import Publish._
//import xerial.sbt.Sonatype.sonatypeCentralHost

publishVersion := "0.23.0"

ThisBuild / organization := "dev.valentiay"
ThisBuild / version := {
  val branch = git.gitCurrentBranch.value
  if (branch == "master") publishVersion.value
  else s"${publishVersion.value}-$branch-SNAPSHOT".replace("/", "_")
}

ThisBuild / publishMavenStyle := true
//ThisBuild / sonatypeCredentialHost := sonatypeCentralHost
ThisBuild / publishTo :=
  (if (!isSnapshot.value) {
     sonatypePublishToBundle.value
   } else {
     Opts.resolver.sonatypeOssSnapshots.headOption
   })

ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/valentiay/phobos"),
    "git@github.com:valentiay/phobos",
  ),
)

ThisBuild / developers := List(
  Developer(
    id = "valentiay",
    name = "Alexander Valentinov",
    email = "valentiay@yandex.ru",
    url = url("https://github.com/valentiay"),
  ),
)

ThisBuild / description := "Fast xml data binding library"
ThisBuild / licenses    := List("Apache 2" -> new URL("http://www.apache.org/licenses/LICENSE-2.0.txt"))
ThisBuild / homepage    := Some(url("https://github.com/valentiay/phobos"))

// Remove all additional repository other than Maven Central from POM
ThisBuild / pomIncludeRepository := { _ => false }

ThisBuild / credentials += Credentials(Path.userHome / ".sbt" / "sonatype_credential")
