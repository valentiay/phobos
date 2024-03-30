package phobos

object testString {
  implicit class StringOps(str: String) {
    def minimized: String = str.trim.replaceAll("\n +", "")
  }
}
