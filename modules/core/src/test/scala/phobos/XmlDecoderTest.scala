package phobos

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import phobos.decoding.{ElementDecoder, XmlDecoder}
import phobos.derivation.semiauto.deriveElementDecoder

class XmlDecoderTest extends AnyWordSpec with Matchers {
  "XmlDecoder" should {
    "not fail on comments and other events before the first element" in {
      case class Foo(bar: String)

      implicit val fooElementDecoder: ElementDecoder[Foo] =
        deriveElementDecoder[Foo]
      implicit val xmlDecoder: XmlDecoder[Foo] =
        XmlDecoder.fromElementDecoder("foo")

      val result =
        XmlDecoder[Foo]
          .decode(
            """<?xml version="1.1" encoding="UTF-8"?>
              |
              |
              |
              |<!-- comment -->
              |
              |<foo>
              |    <bar>bar</bar>
              |</foo>
              |""".stripMargin,
          )

      result shouldBe Right(Foo("bar"))
    }
  }
}
