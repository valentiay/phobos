package phobos.decoding

import javax.xml.stream.XMLStreamConstants

import phobos.decoding.XmlDecoder.createStreamReader

trait XmlDecoderIterable[A] { xmlDecoder: XmlDecoder[A] =>
  def decodeFromIterable(
      iterable: IterableOnce[Array[Byte]],
      charset: String = "UTF-8",
  ): Either[DecodingError, A] = {
    val sr: XmlStreamReader = createStreamReader(charset)
    val cursor              = new Cursor(sr)

    val a = iterable.iterator.foldLeft(elementdecoder) { (decoder: ElementDecoder[A], bytes: Array[Byte]) =>
      sr.getInputFeeder.feedInput(bytes, 0, bytes.length)
      while {
        cursor.next()
        XmlDecoder.isIgnorableEvent(cursor.getEventType)
      } do ()

      if (decoder.result(cursor.history).isRight) {
        decoder
      } else {
        decoder.decodeAsElement(cursor, localname, namespaceuri)
      }
    }
    sr.getInputFeeder.endOfInput()
    a.result(cursor.history)
  }
}
