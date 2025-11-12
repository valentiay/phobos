package phobos

import scala.annotation.nowarn
import scala.deriving.Mirror
import scala.reflect.TypeTest

import phobos.SealedClasses.Animal.animalDecoder
import phobos.configured.ElementCodecConfig
import phobos.decoding._
import phobos.derivation.common.extractSumTypeChild
import phobos.derivation.encoder.deriveElementEncoder
import phobos.derivation.semiauto.deriveXmlEncoder
import phobos.encoding._
import phobos.syntax.attr
import phobos.syntax.discriminator
import phobos.syntax.text
import phobos.testString._

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class DerivationTest extends AnyWordSpec with Matchers {
  import DerivationTest.*

  "ElementEncoder.derived" should {
    "derive for products" in {
      given XmlEncoder[Bar] = XmlEncoder.fromElementEncoder("bar")

      val bar = Bar("d value", Foo(1, "b value", 3.0), 'e')
      val string = """<?xml version='1.0' encoding='UTF-8'?>
                     | <bar>
                     |   <d>d value</d>
                     |   <foo>
                     |     <a>1</a>
                     |     <b>b value</b>
                     |     <c>3.0</c>
                     |   </foo>
                     |   <e>e</e>
                     | </bar>
                   """.stripMargin.minimized

      val encoded = XmlEncoder[Bar].encode(bar)
      assert(encoded == Right(string))
    }

    "derive for sealed traits" in {
      given XmlEncoder[Wild] = XmlEncoder.fromElementEncoder("Wild")
      assert(
        XmlEncoder[Wild].encode(Wild.Tiger) == Right(
          """
            | <?xml version='1.0' encoding='UTF-8'?>
            | <Wild xmlns:ans1="http://www.w3.org/2001/XMLSchema-instance" ans1:type="cat"/>
        """.stripMargin.minimized,
        ),
      )
      assert(
        XmlEncoder[Wild].encode(Wild.Wolf("Coyote")) == Right(
          """
            | <?xml version='1.0' encoding='UTF-8'?>
            | <Wild xmlns:ans1="http://www.w3.org/2001/XMLSchema-instance" ans1:type="dog">Coyote</Wild>
        """.stripMargin.minimized,
        ),
      )
    }

    "derive for sealed traits summons children definitions" in {
      import phobos.configured.naming.{snakeCase, upperSnakeCase}
      import phobos.derivation.semiauto.deriveElementEncoderConfigured

      sealed trait Foo
      case class Bar(shouldBeSnakeCase: String) extends Foo
      case class Qux(shouldBeScreaming: String) extends Foo

      given ElementEncoder[Bar] = deriveElementEncoderConfigured(
        ElementCodecConfig.default.withElementNamesTransformed(snakeCase),
      )
      given ElementEncoder[Qux] = deriveElementEncoderConfigured(
        ElementCodecConfig.default.withElementNamesTransformed(upperSnakeCase),
      )
      given ElementEncoder[Foo] = deriveElementEncoderConfigured(
        ElementCodecConfig.default.usingElementNamesAsDiscriminator,
      )
      given XmlEncoder[Foo] = XmlEncoder.fromElementEncoder("foo")

      val expectedBar =
        "<?xml version='1.0' encoding='UTF-8'?><Bar><should_be_snake_case>bar</should_be_snake_case></Bar>"
      val expectedQux =
        "<?xml version='1.0' encoding='UTF-8'?><Qux><SHOULD_BE_SCREAMING>qux</SHOULD_BE_SCREAMING></Qux>"

      assert(XmlEncoder[Foo].encode(Bar("bar")) == Right(expectedBar))
      assert(XmlEncoder[Foo].encode(Qux("qux")) == Right(expectedQux))
    }

    "derive for enums" in {
      given XmlEncoder[Domestic] = XmlEncoder.fromElementEncoder("Domestic")
      assert(
        XmlEncoder[Domestic].encode(Domestic.Cat) == Right(
          """
            | <?xml version='1.0' encoding='UTF-8'?>
            | <Domestic xmlns:ans1="http://www.w3.org/2001/XMLSchema-instance" ans1:type="tiger"/>
        """.stripMargin.minimized,
        ),
      )
      assert(
        XmlEncoder[Domestic].encode(Domestic.Dog("Pug")) == Right(
          """
            | <?xml version='1.0' encoding='UTF-8'?>
            | <Domestic xmlns:ans1="http://www.w3.org/2001/XMLSchema-instance" ans1:type="wolf">Pug</Domestic>
        """.stripMargin.minimized,
        ),
      )
    }

    "derive for products with sealed traits and enums" in {
      given XmlEncoder[Nature] = XmlEncoder.fromElementEncoder("Nature")

      val res = XmlEncoder[Nature].encode(Nature(Wild.Tiger, Domestic.Dog("Pug")))
      assert(
        res == Right(
          """
            | <?xml version='1.0' encoding='UTF-8'?>
            | <Nature>
            |   <wild xmlns:ans1="http://www.w3.org/2001/XMLSchema-instance" ans1:type="cat"/>
            |   <domestic xmlns:ans2="http://www.w3.org/2001/XMLSchema-instance" ans2:type="wolf">Pug</domestic>
            | </Nature>
          """.stripMargin.minimized,
        ),
      )
    }
  }

  "ElementDecoder.derived" should {
    "derive for products" in {
      given XmlDecoder[Bar] = XmlDecoder.fromElementDecoder("bar")

      val bar = Bar("d value", Foo(1, "b value", 3.0), 'e')
      val string = """<?xml version='1.0' encoding='UTF-8'?>
                     | <bar>
                     |   <d>d value</d>
                     |   <foo>
                     |     <a>1</a>
                     |     <b>b v<![CDATA[al]]>ue</b>
                     |     <c>3.0</c>
                     |   </foo>
                     |   <e>e</e>
                     | </bar>
                   """.stripMargin

      val decoded = XmlDecoder[Bar].decode(string)
      assert(decoded == Right(bar))
    }

    "derive for sealed traits" in {
      given XmlDecoder[Wild] = XmlDecoder.fromElementDecoder("Wild")

      val tigerString = """<?xml version='1.0' encoding='UTF-8'?>
                          | <Wild xmlns:ans1="http://www.w3.org/2001/XMLSchema-instance" ans1:type="cat"/>
        """.stripMargin

      val wolfString = """<?xml version='1.0' encoding='UTF-8'?>
                         | <Wild xmlns:ans1="http://www.w3.org/2001/XMLSchema-instance" ans1:type="dog">Coyote</Wild>
          """.stripMargin

      assert(XmlDecoder[Wild].decode(tigerString) == Right(Wild.Tiger))
      assert(XmlDecoder[Wild].decode(wolfString) == Right(Wild.Wolf("Coyote")))
    }

    "derive for enums" in {
      given XmlDecoder[Domestic] = XmlDecoder.fromElementDecoder("Domestic")

      val catString = """<?xml version='1.0' encoding='UTF-8'?>
                        | <Domestic xmlns:ans1="http://www.w3.org/2001/XMLSchema-instance" ans1:type="tiger"/>
      """.stripMargin

      val dogString =
        """<?xml version='1.0' encoding='UTF-8'?>
          | <Domestic xmlns:ans1="http://www.w3.org/2001/XMLSchema-instance" ans1:type="wolf">Pug</Domestic>
      """.stripMargin

      assert(XmlDecoder[Domestic].decode(catString) == Right(Domestic.Cat))
      assert(XmlDecoder[Domestic].decode(dogString) == Right(Domestic.Dog("Pug")))
    }

    "derive for products with sealed traits and enums" in {
      given XmlDecoder[Nature] = XmlDecoder.fromElementDecoder("Nature")

      val natureString =
        """
          | <?xml version='1.0' encoding='UTF-8'?>
          | <Nature>
          |   <wild xmlns:ans1="http://www.w3.org/2001/XMLSchema-instance" ans1:type="cat"/>
          |   <domestic xmlns:ans2="http://www.w3.org/2001/XMLSchema-instance" ans2:type="wolf">Pug</domestic>
          | </Nature>
          """.stripMargin.minimized

      val nature = Nature(Wild.Tiger, Domestic.Dog("Pug"))
      assert(XmlDecoder[Nature].decode(natureString) == Right(nature))
    }

    "handle case objects properly" in {
      case object Foo derives ElementDecoder
      val string = """<?xml version='1.0' encoding='UTF-8'?>
                     | <Foo/>
                   """.stripMargin.minimized

      given XmlDecoder[Foo.type] = XmlDecoder.fromElementDecoder("Foo")
      assert(XmlDecoder[Foo.type].decode(string) == Right(Foo))
    }
  }
}

object DerivationTest {
  case class Foo(a: Int, b: String, c: Double) derives ElementEncoder, ElementDecoder
  case class Bar(d: String, foo: Foo, e: Char) derives ElementEncoder, ElementDecoder

  sealed trait Wild derives ElementEncoder, ElementDecoder
  object Wild {
    @discriminator("cat") case object Tiger                    extends Wild derives ElementEncoder, ElementDecoder
    @discriminator("dog") case class Wolf(@text breed: String) extends Wild derives ElementEncoder, ElementDecoder
  }

  enum Domestic derives ElementEncoder, ElementDecoder {
    @discriminator("tiger") case Cat
    @discriminator("wolf") case Dog(@text breed: String)
  }

  case class Nature(wild: Wild, domestic: Domestic) derives ElementEncoder, ElementDecoder
}
