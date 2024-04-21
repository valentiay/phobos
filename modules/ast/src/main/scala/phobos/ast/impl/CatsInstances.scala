package phobos.ast.impl

import phobos.ast.XmlEntry

import cats.Eq

trait CatsInstances {
  implicit val eqForAst: Eq[XmlEntry] = Eq.fromUniversalEquals[XmlEntry]
}
