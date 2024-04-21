package phobos.ast

import phobos.Namespace
import phobos.decoding.{DecodingError, XmlDecoder}

import cats.syntax.either._
import com.softwaremill.diffx.generic.auto._
import com.softwaremill.diffx.scalatest.DiffShouldMatcher
import org.scalatest.EitherValues
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class XmlEntryElementDecoderTest extends AnyWordSpec with Matchers with DiffShouldMatcher with EitherValues {

  "XmlEntry decoder" should {
    "decodes simple Xml into ast correctly" in {
      val sampleXml                = """<?xml version='1.0' encoding='UTF-8'?><ast foo="5"><bar>bazz</bar></ast>"""
      val decodedAst               = XmlDecoder.fromElementDecoder[XmlEntry]("ast").decode(sampleXml).value
      val expectedResult: XmlEntry = xml(attr("foo") := 5, node("bar") := "bazz")

      decodedAst shouldMatchTo (expectedResult)
    }

    "decodes complicated Xml into ast correctly" in {
      case object example {
        type ns = example.type
        implicit val ns: Namespace[example.type] = Namespace.mkInstance("https://example.org")
      }

      val sampleXml =
        """<?xml version='1.0' encoding='UTF-8'?><ans1:ast xmlns:ans1="https://example.org" foo="5"><bar>bazz</bar><array foo2="true" foo3="false"><elem>11111111111111</elem><elem>11111111111112</elem></array><nested><scala>2.13</scala><dotty>0.13</dotty><scala-4/></nested></ans1:ast>"""

      val decodedAst = XmlDecoder.fromElementDecoderNs[XmlEntry, example.ns]("ast").decode(sampleXml)
      val expectedResult: XmlEntry = xml(attr("foo") := 5)(
        node("bar") := "bazz",
        node("array") := xml(
          attr("foo2") := true,
          attr("foo3") := false,
        )(
          node("elem") := 11111111111111L,
          node("elem") := 11111111111112L,
        ),
        node("nested") := xml(
          node("scala")   := 2.13,
          node("dotty")   := 0.13,
          node("scala-4") := xml.empty,
        ),
      )

      decodedAst shouldMatchTo (expectedResult.asRight[DecodingError])
    }

    "works fine when for elements with same name" in {

      val n: XmlEntry = xml(
        node("k") :=
          xml(
            node("k") := "gbq",
          ),
      )

      val encoded = phobos.encoding.XmlEncoder.fromElementEncoder[XmlEntry]("ast").encode(n)

      val result = encoded.flatMap(XmlDecoder.fromElementDecoder[XmlEntry]("ast").decode(_))

      assert(result.map(util.AstTransformer.sortNodeValues) == Right(util.AstTransformer.sortNodeValues(n)))
    }
  }
}
