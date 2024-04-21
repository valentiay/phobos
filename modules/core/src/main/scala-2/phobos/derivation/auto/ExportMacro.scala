package phobos.derivation.auto

import scala.reflect.macros.blackbox

import phobos.decoding.ElementDecoder
import phobos.encoding.ElementEncoder

class AutoMacro(val c: blackbox.Context) {
  import c.universe._

  def autoEncoder[A: WeakTypeTag]: Expr[Auto[ElementEncoder[A]]] = {
    c.Expr[Auto[ElementEncoder[A]]](q"""new _root_.phobos.derivation.auto.Auto(
       _root_.phobos.derivation.semiauto.deriveElementEncoder[${weakTypeOf[A]}]
     )""")
  }

  def autoDecoder[A: WeakTypeTag]: Expr[Auto[ElementDecoder[A]]] = {
    c.Expr[Auto[ElementDecoder[A]]](q"""new _root_.phobos.derivation.auto.Auto(
       _root_.phobos.derivation.semiauto.deriveElementDecoder[${weakTypeOf[A]}]
     )""")
  }
}
