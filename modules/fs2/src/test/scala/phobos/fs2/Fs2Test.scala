package phobos.fs2

import scala.annotation.nowarn

import phobos.decoding.ElementDecoder
import phobos.decoding.XmlDecoder
import phobos.derivation.semiauto.deriveElementDecoder
import phobos.derivation.semiauto.deriveXmlDecoder
import phobos.syntax.text

import cats.effect.IO
import cats.effect.unsafe.{IORuntime, IORuntimeConfig, Scheduler}
import fs2.Stream
import org.scalatest.wordspec.AsyncWordSpec

@nowarn("msg=is never used")
class Fs2Test extends AsyncWordSpec {
  val (scheduler, shutdown) = Scheduler.createDefaultScheduler()
  implicit val ioRuntime: IORuntime =
    IORuntime(executionContext, executionContext, scheduler, shutdown, IORuntimeConfig.apply())
  "Fs2 decoder" should {
    "decode case classes correctly" in {
      case class Bar(@text txt: Int)
      implicit val barDecoder: ElementDecoder[Bar] = deriveElementDecoder

      case class Foo(qux: Int, maybeBar: Option[Bar], bars: List[Bar])
      implicit val fooDecoder: XmlDecoder[Foo] = deriveXmlDecoder("foo")

      val xml = """
                  |<foo>
                  | <qux>1234</qux>
                  | <bars>2</bars>
                  | <maybeBar>1</maybeBar>
                  | <bars>3</bars>
                  |</foo>
                  |""".stripMargin

      val foo    = Foo(1234, Some(Bar(1)), List(Bar(2), Bar(3)))
      val stream = Stream.iterable[IO, Array[Byte]](xml.map(x => Array(x.toByte)))
      XmlDecoder[Foo]
        .decodeFromStream(stream)
        .map(result => assert(result == foo))
        .unsafeToFuture()
    }
  }
}
