package phobos.derevo

import derevo.{Derevo, Derivation, delegating}
import phobos.encoding.XmlEncoder

@delegating("phobos.derivation.semiauto.deriveXmlEncoder")
object xmlEncoder extends Derivation[XmlEncoder] {

  def apply[A](arg: String): XmlEncoder[A] = macro Derevo.delegateParam[XmlEncoder, A, String]

  def apply[A, NS](arg1: String, arg2: NS): XmlEncoder[A] = macro Derevo.delegateParams2[XmlEncoder, A, String, NS]
}
