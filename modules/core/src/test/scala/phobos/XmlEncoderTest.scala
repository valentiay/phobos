package phobos

import phobos.derivation.semiauto.deriveXmlEncoder
import phobos.encoding.XmlEncoder

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class XmlEncoderTest extends AnyWordSpec with Matchers {
  "XmlEncoder with config" should {
    "ignore prolog if configured" in {
      final case class Foo(a: Int, b: String, c: Double)
      implicit val fooEncoder: XmlEncoder[Foo] = deriveXmlEncoder("Foo")

      XmlEncoder[Foo].encodeWithConfig(Foo(1, "abc", 1.0), XmlEncoder.defaultConfig.withoutProlog) shouldBe
        Right("<Foo><a>1</a><b>abc</b><c>1.0</c></Foo>")
    }

    "not ignore prolog by default" in {
      final case class Foo(a: Int, b: String, c: Double)
      implicit val fooEncoder: XmlEncoder[Foo] = deriveXmlEncoder("Foo")

      XmlEncoder[Foo].encodeWithConfig(Foo(1, "abc", 1.0), XmlEncoder.defaultConfig) shouldBe
        Right("<?xml version='1.0' encoding='UTF-8'?><Foo><a>1</a><b>abc</b><c>1.0</c></Foo>")
    }

    "overwrite prolog information if configured" in {
      final case class Foo(a: Int, b: String, c: Double)
      implicit val fooEncoder: XmlEncoder[Foo] = deriveXmlEncoder("Foo")

      XmlEncoder[Foo]
        .encodeWithConfig(Foo(1, "abc", 1.0), XmlEncoder.XmlEncoderConfig("UTF-16", "1.1", writeProlog = true)) shouldBe
        Right("<?xml version='1.1' encoding='UTF-16'?><Foo><a>1</a><b>abc</b><c>1.0</c></Foo>")
    }

    "preserve XML 1.0 whitespace characters (\\t, \\n, \\r) in element text" in {
      // Real-world freeform XML element text — multi-line property descriptions,
      // postal addresses, customer messages — routinely contains `\n` and `\t`
      // characters. XML 1.0 §2.2 admits #x9, #xA, #xD as legal characters; before
      // this fix `filterXmlText` silently stripped them, mangling the text on the
      // wire (downstream consumers reported multi-paragraph descriptions arriving
      // as a single line of run-on text).
      final case class Listing(description: String)
      implicit val listingEncoder: XmlEncoder[Listing] = deriveXmlEncoder("Listing")

      val description = "Charming 2-bedroom cottage.\n\nFeatures:\n\tStone fireplace\n\tPrivate dock"

      XmlEncoder[Listing].encodeWithConfig(Listing(description), XmlEncoder.defaultConfig.withoutProlog) shouldBe
        Right(s"<Listing><description>$description</description></Listing>")
    }
  }
}
