package phobos.fs2

import javax.xml.stream.XMLStreamConstants

import phobos.decoding.{Cursor, ElementDecoder, XmlDecoder, XmlStreamReader}

import cats.MonadError
import cats.syntax.flatMap._
import fs2.Stream

trait Fs2Ops {
  implicit def decoderOps[A](xmlDecoder: XmlDecoder[A]): DecoderOps[A] = new DecoderOps[A](xmlDecoder)
}

class DecoderOps[A](private val xmlDecoder: XmlDecoder[A]) extends AnyVal {
  def decodeFromStream[F[_], G[_]](
      stream: Stream[F, Array[Byte]],
      charset: String = "UTF-8",
  )(implicit compiler: Stream.Compiler[F, G], monadError: MonadError[G, Throwable]): G[A] = {
    val sr: XmlStreamReader = XmlDecoder.createStreamReader(charset)
    val cursor              = new Cursor(sr)

    stream
      .fold[ElementDecoder[A]](xmlDecoder.elementdecoder) { (decoder, bytes) =>
        sr.getInputFeeder.feedInput(bytes, 0, bytes.length)
        cursor.next()
        while (XmlDecoder.isIgnorableEvent(cursor.getEventType)) {
          cursor.next()
        }

        if (decoder.result(cursor.history).isRight) {
          decoder
        } else {
          decoder.decodeAsElement(cursor, xmlDecoder.localname, xmlDecoder.namespaceuri)
        }
      }
      .map(_.result(cursor.history))
      .compile
      .lastOrError
      .flatMap(result => MonadError[G, Throwable].fromEither(result))
  }
}
