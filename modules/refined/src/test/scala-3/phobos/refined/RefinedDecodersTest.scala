package phobos.refined

import scala.annotation.nowarn

import phobos.decoding.{ElementDecoder, XmlDecoder}
import phobos.derivation.semiauto._
import phobos.syntax.{attr, text}
import phobos.testString._

import eu.timepit.refined.api.Refined
import eu.timepit.refined.refineV
import eu.timepit.refined.string.MatchesRegex
import eu.timepit.refined.types.numeric.NonNegLong
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

@nowarn("msg=is never used")
class RefinedDecodersTest extends AnyWordSpec with Matchers {
  type NumericAtLeastTwo = MatchesRegex["[0-9]{2,}"]

  "refined decoder" should {
    "decode attributes correctly" in {
      case class Test(x: Int, @attr y: Refined[String, NumericAtLeastTwo])
      given XmlDecoder[Test] = deriveXmlDecoder[Test]("test")

      val sampleXml = """
                        | <?xml version='1.0' encoding='UTF-8'?>
                        | <test y="123">
                        |   <x>2</x>
                        | </test>
       """.stripMargin.minimized

      val expectedResult = Test(2, refineV[NumericAtLeastTwo]("123").toOption.get)

      XmlDecoder[Test].decode(sampleXml) shouldEqual Right(expectedResult)

    }

    "decode elements correctly" in {
      case class Test(x: Int, y: Refined[String, NumericAtLeastTwo])
      given XmlDecoder[Test] = deriveXmlDecoder[Test]("test")

      val sampleXml = """
                        | <?xml version='1.0' encoding='UTF-8'?>
                        | <test>
                        |   <x>2</x>
                        |   <y>123</y>
                        | </test>
       """.stripMargin.minimized

      val expectedResult = Test(2, refineV[NumericAtLeastTwo]("123").toOption.get)

      XmlDecoder[Test].decode(sampleXml) shouldEqual Right(expectedResult)

    }

    "decode text correctly" in {
      case class Foo(@attr bar: Int, @text baz: NonNegLong)
      given ElementDecoder[Foo] = deriveElementDecoder[Foo]
      case class Qux(str: String, foo: Foo)
      given XmlDecoder[Qux] = deriveXmlDecoder[Qux]("qux")

      val sampleXml =
        """
          | <?xml version='1.0' encoding='UTF-8'?>
          | <qux>
          |   <str>42</str>
          |   <foo bar="42">1000</foo>
          | </qux>
        """.stripMargin.minimized

      val expectedResult = Qux("42", Foo(42, NonNegLong.unsafeFrom(1000L)))
      XmlDecoder[Qux].decode(sampleXml) shouldEqual Right(expectedResult)
    }

    "provide verbose errors" in {

      case class Test2(x: Int, y: Refined[String, NumericAtLeastTwo])
      given XmlDecoder[Test2] = deriveXmlDecoder[Test2]("test")
      case class Foo2(@attr bar: Int, @text baz: NonNegLong)
      given ElementDecoder[Foo2] = deriveElementDecoder[Foo2]
      case class Qux2(str: String, foo: Foo2)
      given XmlDecoder[Qux2] = deriveXmlDecoder[Qux2]("qux")

      val sampleXml0 = """
                         | <?xml version='1.0' encoding='UTF-8'?>
                         | <test>
                         |   <x>2</x>
                         |   <y>1</y>
                         | </test>
         """.stripMargin.minimized

      XmlDecoder[Test2]
        .decode(sampleXml0)
        .left
        .map(_.text) shouldEqual Left(
        """Failed to verify eu.timepit.refined.string.MatchesRegex refinement for value=1 of raw type java.lang.String: Predicate failed: "1".matches("[0-9]{2,}").""",
      )

      val sampleXml1 =
        """
          | <?xml version='1.0' encoding='UTF-8'?>
          | <qux>
          |   <str>42</str>
          |   <foo bar="42">-1000</foo>
          | </qux>
        """.stripMargin.minimized

      XmlDecoder[Qux2]
        .decode(sampleXml1)
        .left
        .map(_.text) shouldEqual Left(
        // ClassTag erases the `NonNegative` alias to its underlying `Not[Less[_0]]`.
        """Failed to verify eu.timepit.refined.boolean.Not refinement for value=-1000 of raw type long: Predicate (-1000 < 0) did not fail.""",
      )

    }
  }
}
