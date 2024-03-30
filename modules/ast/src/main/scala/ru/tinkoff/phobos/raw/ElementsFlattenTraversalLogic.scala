package phobos.raw

import phobos.ast.XmlLeaf
import phobos.raw.ElementsFlattenTraversalLogic.Accumulator
import phobos.traverse.DecodingTraversalLogic
import scala.collection.mutable.ListBuffer

class ElementsFlattenTraversalLogic private () extends DecodingTraversalLogic[Accumulator, ElementsFlatten] {
  override def newAcc(): Accumulator = Accumulator()

  override def onFinish(acc: Accumulator): ElementsFlatten =
    ElementsFlatten(acc.elements.toList: _*)

  override def combine(acc: Accumulator, field: String, intermediateResult: ElementsFlatten): Accumulator = {
    acc.elements ++= intermediateResult.elems
    acc
  }

  override def onText(acc: Accumulator, elemName: String, text: XmlLeaf): Accumulator = {
    acc.elements += (elemName -> text)
    acc
  }
}

object ElementsFlattenTraversalLogic {
  case class Accumulator(elements: ListBuffer[(String, XmlLeaf)] = ListBuffer.empty)

  val instance = new ElementsFlattenTraversalLogic()
}
