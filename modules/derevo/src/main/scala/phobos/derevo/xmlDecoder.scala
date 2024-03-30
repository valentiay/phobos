package phobos.derevo

import derevo.{Derevo, Derivation, delegating}
import phobos.decoding.XmlDecoder

@delegating("phobos.derivation.semiauto.deriveXmlDecoder")
object xmlDecoder extends Derivation[XmlDecoder] {

  def apply[A](arg: String): XmlDecoder[A] = macro Derevo.delegateParam[XmlDecoder, A, String]

  def apply[A, NS](arg1: String, arg2: NS): XmlDecoder[A] = macro Derevo.delegateParams2[XmlDecoder, A, String, NS]
}
