package phobos.refined

import phobos.derivation.semiauto._
import phobos.encoding.{ElementEncoder, XmlEncoder}
import phobos.syntax.{attr, text}
import phobos.testString._

import eu.timepit.refined.api.Refined
import eu.timepit.refined.refineV
import eu.timepit.refined.string.MatchesRegex
import eu.timepit.refined.types.numeric.NonNegLong
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class RefinedEncodersTest extends AnyWordSpec with Matchers {
  type NumericAtLeastTwo = MatchesRegex["[0-9]{2,}"]

  "refined encoder" should {

    "encode attributes correctly" in {
      case class Test(x: Int, @attr y: Refined[String, NumericAtLeastTwo])
      given XmlEncoder[Test] = deriveXmlEncoder[Test]("test")

      val value = Test(2, refineV[NumericAtLeastTwo]("123").toOption.get)

      val expectedResult = """
                             | <?xml version='1.0' encoding='UTF-8'?>
                             | <test y="123">
                             |   <x>2</x>
                             | </test>
       """.stripMargin.minimized

      XmlEncoder[Test].encode(value) shouldEqual Right(expectedResult)
    }

    "encode elements correctly" in {
      case class Test(x: Int, y: Refined[String, NumericAtLeastTwo])
      given XmlEncoder[Test] = deriveXmlEncoder[Test]("test")

      val value = Test(2, refineV[NumericAtLeastTwo]("123").toOption.get)

      val expectedResult = """
                             | <?xml version='1.0' encoding='UTF-8'?>
                             | <test>
                             |   <x>2</x>
                             |   <y>123</y>
                             | </test>
        """.stripMargin.minimized

      XmlEncoder[Test].encode(value) shouldEqual Right(expectedResult)
    }

    "encode text correctly" in {
      case class Foo(@attr bar: Int, @text baz: NonNegLong)
      given ElementEncoder[Foo] = deriveElementEncoder[Foo]
      case class Qux(str: String, foo: Foo)
      given XmlEncoder[Qux] = deriveXmlEncoder[Qux]("qux")

      val qux = Qux("42", Foo(42, NonNegLong.unsafeFrom(1000L)))
      val xml = XmlEncoder[Qux].encode(qux)
      val string =
        """
          | <?xml version='1.0' encoding='UTF-8'?>
          | <qux>
          |   <str>42</str>
          |   <foo bar="42">1000</foo>
          | </qux>
          """.stripMargin.minimized
      assert(xml == Right(string))
    }
  }
}
