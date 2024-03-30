package phobos.derevo

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class DerevoTest extends AnyWordSpec with Matchers {
  "Derevo" should {
    "derive xml encoder without namespace" in {
      """
        | import derevo.derive
        | import phobos.derevo.xmlEncoder
        | import phobos.encoding.XmlEncoder
        | @derive(xmlEncoder("foo"))
        | case class Foo(int: Int, string: String, double: Double)
        | implicitly[XmlEncoder[Foo]]
      """.stripMargin should compile
    }

    "derive xml encoder with namespace" in {
      """
        | import derevo.derive
        | import phobos.annotations.XmlnsDef
        | import phobos.derevo.xmlEncoder
        | import phobos.encoding.XmlEncoder
        | @XmlnsDef("example.org")
        | case object org
        | @derive(xmlEncoder("foo", org))
        | case class Foo(int: Int, string: String, double: Double)
        | implicitly[XmlEncoder[Foo]]
      """.stripMargin should compile
    }

    "derive xml decoder without namespace" in {
      """
        | import derevo.derive
        | import phobos.derevo.xmlDecoder
        | import phobos.decoding.XmlDecoder
        | @derive(xmlDecoder("foo"))
        | case class Foo(int: Int, string: String, double: Double)
        | implicitly[XmlDecoder[Foo]]
      """.stripMargin should compile
    }

    "derive xml decoder with namespace" in {
      """
        | import derevo.derive
        | import phobos.annotations.XmlnsDef
        | import phobos.derevo.xmlDecoder
        | import phobos.decoding.XmlDecoder
        | @XmlnsDef("example.org")
        | case object org
        | @derive(xmlDecoder("foo", org))
        | case class Foo(int: Int, string: String, double: Double)
        | implicitly[XmlDecoder[Foo]]
      """.stripMargin should compile
    }
  }
}
