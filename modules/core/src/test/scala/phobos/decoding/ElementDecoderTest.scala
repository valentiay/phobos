package phobos.decoding

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import phobos.configured.ElementCodecConfig
import phobos.derivation.semiauto.{deriveXmlDecoder, deriveXmlDecoderConfigured}

import java.time.OffsetDateTime

class ElementDecoderTest extends AnyWordSpec with Matchers {
  "ElementDecoder instance" should {
    "exists for OffsetDateTime and works properly" in {

      case class Foo(date: OffsetDateTime)
      implicit val xmlDecoder: XmlDecoder[Foo] = deriveXmlDecoder[Foo]("Foo")

      XmlDecoder[Foo].decode("<Foo><date>2019-10-27T18:27:26.1279855+05:00</date></Foo>") match {
        case Left(failure) => fail(s"Decoding result expected, got: ${failure.getMessage}")
        case Right(value)  => value.date shouldBe OffsetDateTime.parse("2019-10-27T18:27:26.1279855+05:00");
      }
    }

    "ignore namespaces by configuration" in {

      case class Foo(boo: Int)
      implicit val xmlDecoder: XmlDecoder[Foo] = deriveXmlDecoderConfigured[Foo]("Foo", ElementCodecConfig.default.withIgnoreNamespaces())

      XmlDecoder[Foo].decode("""<Foo xmlns="http://example.com"><boo>1</boo></Foo>""") match {
        case Left(failure) => fail(s"Decoding result expected, got: ${failure.getMessage}")
        case Right(value) => value.boo shouldBe 1
      }

      XmlDecoder[Foo].decode(
        """
          |<Foo xmlns="http://example.com" xmlns:a="http://example.com">
          |  <boo>1</boo>
          |</Foo>""".stripMargin) match {
        case Left(failure) => fail(s"Decoding result expected, got: ${failure.getMessage}")
        case Right(value) => value.boo shouldBe 1
      }

      XmlDecoder[Foo].decode(
        """
          |<Foo xmlns="http://example.com" xmlns:a="http://example.com">
          |  <boo xmlns="http://example.com">1</boo>
          |</Foo>""".stripMargin) match {
        case Left(failure) => fail(s"Decoding result expected, got: ${failure.getMessage}")
        case Right(value) => value.boo shouldBe 1
      }

      XmlDecoder[Foo].decode(
        """
          |<Foo xmlns="http://example.com" xmlns:a="http://example.com">
          |  <a:boo xmlns="http://example.com">1</a:boo>
          |</Foo>""".stripMargin) match {
        case Left(failure) => fail(s"Decoding result expected, got: ${failure.getMessage}")
        case Right(value) => value.boo shouldBe 1
      }
    }
  }
}
