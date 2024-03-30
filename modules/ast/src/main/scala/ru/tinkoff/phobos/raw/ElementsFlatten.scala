package phobos.raw

import phobos.ast._
import phobos.decoding.ElementDecoder
import phobos.traverse.GenericElementDecoder

/** Data type containing pairs of the element name and it's text.
  */
case class ElementsFlatten(elems: (String, XmlLeaf)*)

object ElementsFlatten {

  /** Allows to decode [[ElementsFlatten]] from arbitrary XML node
    */
  implicit val elementsFlattenDecoder: ElementDecoder[ElementsFlatten] =
    GenericElementDecoder(ElementsFlattenTraversalLogic.instance)
}
