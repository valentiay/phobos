package phobos.derivation

import phobos.decoding.ElementDecoder
import phobos.encoding.ElementEncoder

import scala.reflect.macros.blackbox

class ExportMacro(val c: blackbox.Context) {
  import c.universe._

  def exportEncoder[A: WeakTypeTag]: Expr[Exported[ElementEncoder[A]]] = {
    c.Expr[Exported[ElementEncoder[A]]](q"""new _root_.phobos.derivation.Exported(
       _root_.phobos.derivation.semiauto.deriveElementEncoder[${weakTypeOf[A]}]
     )""")
  }

  def exportDecoder[A: WeakTypeTag]: Expr[Exported[ElementDecoder[A]]] = {
    c.Expr[Exported[ElementDecoder[A]]](q"""new _root_.phobos.derivation.Exported(
       _root_.phobos.derivation.semiauto.deriveElementDecoder[${weakTypeOf[A]}]
     )""")
  }
}
