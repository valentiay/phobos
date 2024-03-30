package phobos.ast.impl

import cats.Eq
import phobos.ast.XmlEntry

trait CatsInstances {
  implicit val eqForAst: Eq[XmlEntry] = Eq.fromUniversalEquals[XmlEntry]
}
