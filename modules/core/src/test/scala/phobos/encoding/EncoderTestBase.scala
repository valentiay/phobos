package phobos.encoding

import java.io.OutputStream

import com.fasterxml.aalto.out._

trait EncoderTestBase {
  def buildStreamWriter(wc: WriterConfig, os: OutputStream): PhobosStreamWriter = {
    val writer = new Utf8XmlWriter(wc, os)

    new PhobosStreamWriter(
      new NonRepairingStreamWriter(
        wc,
        writer,
        wc.getUtf8Symbols(writer),
      ),
    )
  }
}
