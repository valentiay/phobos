package phobos.akka_http

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class SoapTest extends AnyWordSpec with Matchers {
  "Soap codecs" should {
    "be found for envelope" in {
      """
       | import phobos.encoding.XmlEncoder
       | import phobos.decoding.XmlDecoder
       | import phobos.annotations.ElementCodec
       | @ElementCodec
       | case class Header(foo: Int)
       | @ElementCodec
       | case class Body(bar: String)
       | implicitly[XmlEncoder[Envelope[Header, Body]]]
       | implicitly[XmlDecoder[Envelope[Header, Body]]]
      """.stripMargin should compile
    }

    "be found for headless envelope" in {
      """
       | import phobos.encoding.XmlEncoder
       | import phobos.decoding.XmlDecoder
       | import phobos.annotations.ElementCodec
       | @ElementCodec
       | case class Body(bar: String)
       | implicitly[XmlEncoder[HeadlessEnvelope[Body]]]
       | implicitly[XmlDecoder[HeadlessEnvelope[Body]]]
      """.stripMargin should compile
    }
  }
}
