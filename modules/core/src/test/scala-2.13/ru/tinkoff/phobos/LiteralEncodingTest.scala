package phobos

import org.scalatest.wordspec.AnyWordSpec
import phobos.annotations.XmlCodec
import phobos.encoding.XmlEncoder
import phobos.syntax.{attr, text}
import phobos.testString._

class LiteralEncodingTest extends AnyWordSpec {
  "Literal encoders" should {
    "encode attributes with literal type" in {
      @XmlCodec("foo")
      final case class Foo(@attr status: "Ok")

      val string = XmlEncoder[Foo].encode(Foo("Ok"))
      assert(
        string ==
          Right("""<?xml version='1.0' encoding='UTF-8'?>
            | <foo status="Ok"/>
          """.stripMargin.minimized),
      )
    }

    "encode elements with literal type" in {
      @XmlCodec("foo")
      final case class Foo(status: "Ok")

      val string = XmlEncoder[Foo].encode(Foo("Ok"))
      assert(
        string ==
          Right("""<?xml version='1.0' encoding='UTF-8'?>
            | <foo><status>Ok</status></foo>
          """.stripMargin.minimized),
      )
    }

    "encode text with literal type" in {
      @XmlCodec("foo")
      final case class Foo(@text status: "Ok")

      val string = XmlEncoder[Foo].encode(Foo("Ok"))
      assert(
        string ==
          Right("""<?xml version='1.0' encoding='UTF-8'?>
            | <foo>Ok</foo>
          """.stripMargin.minimized),
      )
    }
  }
}
