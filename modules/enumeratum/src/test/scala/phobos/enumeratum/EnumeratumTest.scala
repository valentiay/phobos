package phobos.enumeratum

import phobos.decoding.XmlDecoder
import phobos.derivation.semiauto._
import phobos.encoding.XmlEncoder
import phobos.syntax._
import phobos.testString._

import enumeratum._
import org.scalatest.Assertion
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class EnumeratumTest extends AnyWordSpec with Matchers {
  "Enum codecs" should {
    "encode enums" in {
      sealed trait Foo extends EnumEntry with Product with Serializable
      object Foo extends XmlEnum[Foo] with Enum[Foo] {
        val values = findValues

        case object Foo1 extends Foo
        case object Foo2 extends Foo
        case object Foo3 extends Foo
      }
      import Foo._
      case class Bar(d: String, foo: Foo, e: Char)
      object Bar {
        implicit lazy val xmlEncoder: XmlEncoder[Bar] = deriveXmlEncoder[Bar]("bar")
        implicit lazy val xmlDecoder: XmlDecoder[Bar] = deriveXmlDecoder[Bar]("bar")
      }
      case class Baz(@attr f: Foo, @text text: Foo)
      object Baz {
        implicit lazy val xmlEncoder: XmlEncoder[Baz] = deriveXmlEncoder[Baz]("baz")
        implicit lazy val xmlDecoder: XmlDecoder[Baz] = deriveXmlDecoder[Baz]("baz")
      }
      val bar1 = Bar("d value", Foo1, 'e')
      val bar2 = Bar("d value", Foo2, 'e')
      val bar3 = Bar("another one value", Foo3, 'v')
      val baz  = Baz(Foo1, Foo2)
      val xml1 = XmlEncoder[Bar].encode(bar1)
      val xml2 = XmlEncoder[Bar].encode(bar2)
      val xml3 = XmlEncoder[Bar].encode(bar3)
      val xml4 = XmlEncoder[Baz].encode(baz)
      val string1 =
        """
          | <?xml version='1.0' encoding='UTF-8'?>
          | <bar>
          |   <d>d value</d>
          |   <foo>
          |     Foo1
          |   </foo>
          |   <e>e</e>
          | </bar>
        """.stripMargin.minimized
      val string2 =
        """
          | <?xml version='1.0' encoding='UTF-8'?>
          | <bar>
          |   <d>d value</d>
          |   <foo>
          |     Foo2
          |   </foo>
          |   <e>e</e>
          | </bar>
        """.stripMargin.minimized
      val string3 =
        """
          | <?xml version='1.0' encoding='UTF-8'?>
          | <bar>
          |   <d>another one value</d>
          |   <foo>
          |     Foo3
          |   </foo>
          |   <e>v</e>
          | </bar>
        """.stripMargin.minimized
      val string4 =
        """
          | <?xml version='1.0' encoding='UTF-8'?>
          | <baz f="Foo1">Foo2</baz>
        """.stripMargin.minimized
      assert(
        xml1 == Right(string1) &&
          xml2 == Right(string2) &&
          xml3 == Right(string3) &&
          xml4 == Right(string4),
      )
    }

    def pure(str: String): List[Array[Byte]] =
      List(str.getBytes("UTF-8"))

    def fromIterable(str: String): List[Array[Byte]] =
      str.toList.map(c => Array(c.toByte))

    def decodeEnums(toList: String => List[Array[Byte]]): Assertion = {
      sealed trait Foo extends EnumEntry with Product with Serializable
      object Foo extends XmlEnum[Foo] with Enum[Foo] {
        val values = findValues

        case object Foo1 extends Foo

        case object Foo2 extends Foo

        case object Foo3 extends Foo

      }
      import Foo._
      case class Bar(d: String, foo: Foo, e: Char)
      object Bar {
        implicit lazy val xmlEncoder: XmlEncoder[Bar] = deriveXmlEncoder[Bar]("bar")
        implicit lazy val xmlDecoder: XmlDecoder[Bar] = deriveXmlDecoder[Bar]("bar")
      }
      case class Baz(@attr f: Foo, @text text: Foo)
      object Baz {
        implicit lazy val xmlEncoder: XmlEncoder[Baz] = deriveXmlEncoder[Baz]("baz")
        implicit lazy val xmlDecoder: XmlDecoder[Baz] = deriveXmlDecoder[Baz]("baz")
      }
      val bar1 = Bar("d value", Foo1, 'e')
      val bar2 = Bar("d value", Foo2, 'e')
      val bar3 = Bar("another one value", Foo3, 'v')
      val baz  = Baz(Foo1, Foo2)

      val string1 =
        """<?xml version='1.0' encoding='UTF-8'?>
          | <bar>
          |   <d>d value</d>
          |   <foo>Foo1</foo>
          |   <e>e</e>
          | </bar>
        """.stripMargin
      val string2 =
        """<?xml version='1.0' encoding='UTF-8'?>
          | <bar>
          |   <d>d value</d>
          |   <foo>Foo2</foo>
          |   <e>e</e>
          | </bar>
        """.stripMargin
      val string3 =
        """<?xml version='1.0' encoding='UTF-8'?>
          | <bar>
          |   <d>another one value</d>
          |   <foo>Foo3</foo>
          |   <e>v</e>
          | </bar>
        """.stripMargin
      val string4 =
        """<?xml version='1.0' encoding='UTF-8'?>
          | <baz f="Foo1">Foo2</baz>
        """.stripMargin
      val decoded1 = XmlDecoder[Bar].decodeFromIterable(toList(string1))
      val decoded2 = XmlDecoder[Bar].decodeFromIterable(toList(string2))
      val decoded3 = XmlDecoder[Bar].decodeFromIterable(toList(string3))
      val decoded4 = XmlDecoder[Baz].decodeFromIterable(toList(string4))
      assert(decoded1 == Right(bar1) && decoded2 == Right(bar2) && decoded3 == Right(bar3) && decoded4 == Right(baz))
    }

    "decode enums sync" in decodeEnums(pure)
    "decode enums async" in decodeEnums(fromIterable)
  }
}
