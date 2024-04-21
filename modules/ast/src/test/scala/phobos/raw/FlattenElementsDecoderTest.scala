package phobos.raw

import phobos.Namespace
import phobos.ast._
import phobos.decoding.XmlDecoder

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class FlattenElementsDecoderTest extends AnyWordSpec with Matchers {

  "XmlEntry decoder" should {
    "decodes simple Xml into ast correctly" in {
      val xml        = """<?xml version='1.0' encoding='UTF-8'?><ast foo="5"><bar>bazz</bar></ast>"""
      val decodedRaw = XmlDecoder.fromElementDecoder[ElementsFlatten]("ast").decode(xml)
      assert(
        decodedRaw.contains(
          ElementsFlatten(
            "bar" -> "bazz",
          ),
        ),
      )
    }

    "decodes complicated Xml into ast correctly" in {
      case object example {
        type ns = example.type
        implicit val ns: Namespace[example.type] = Namespace.mkInstance("https://example.org")
      }

      val xml =
        """<?xml version='1.0' encoding='UTF-8'?><ans1:ast xmlns:ans1="https://example.org" foo="5"><bar>bazz</bar><array foo2="true" foo3="false"><elem>11111111111111</elem><elem>11111111111112</elem></array><nested><scala>2.13</scala><dotty>0.13</dotty><scala-4/></nested></ans1:ast>"""

      val decodedRaw = XmlDecoder.fromElementDecoderNs[ElementsFlatten, example.ns]("ast").decode(xml)
      assert(
        decodedRaw.contains(
          ElementsFlatten(
            "bar"   -> "bazz",
            "elem"  -> 11111111111111L,
            "elem"  -> 11111111111112L,
            "scala" -> 2.13,
            "dotty" -> 0.13,
          ),
        ),
      )
    }
  }
}
