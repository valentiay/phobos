package phobos.decoding

import java.time.OffsetDateTime

import phobos.derivation.semiauto._
import phobos.syntax.attr

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class AttributeDecoderTest extends AnyWordSpec with Matchers {
  "AttributeDecoder instance" should {
    "exists for OffsetDateTime and works properly" in {

      case class Foo(@attr date: OffsetDateTime)
      implicit val xmlDecoder: XmlDecoder[Foo] = deriveXmlDecoder[Foo]("Foo")

      XmlDecoder[Foo].decode("<Foo date=\"2019-10-27T18:27:26.1279855+05:00\"/>") match {
        case Left(failure) => fail(s"Decoding result expected, got: ${failure.getMessage}")
        case Right(value)  => value.date shouldBe OffsetDateTime.parse("2019-10-27T18:27:26.1279855+05:00");
      }
    }
  }
}
