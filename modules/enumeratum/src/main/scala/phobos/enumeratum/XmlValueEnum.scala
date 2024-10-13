package phobos.enumeratum

import phobos.decoding._
import phobos.encoding._

import enumeratum.values._

sealed trait XmlValueEnum[V, E <: ValueEnumEntry[V]] {
  _enum: ValueEnum[V, E] =>

  implicit def elementEncoder: ElementEncoder[E]
  implicit def attributeEncoder: AttributeEncoder[E]
  implicit def textEncoder: TextEncoder[E]
  implicit def elementDecoder: ElementDecoder[E]
  implicit def attributeDecoder: AttributeDecoder[E]
  implicit def textDecoder: TextDecoder[E]

}

trait IntXmlValueEnum[E <: IntEnumEntry] extends XmlValueEnum[Int, E] {
  self: IntEnum[E] =>
  implicit val elementEncoder: ElementEncoder[E]     = XmlValueEnum.elementEncoder[Int, E]
  implicit val attributeEncoder: AttributeEncoder[E] = XmlValueEnum.attributeEncoder[Int, E]
  implicit val textEncoder: TextEncoder[E]           = XmlValueEnum.textEncoder[Int, E]
  implicit val elementDecoder: ElementDecoder[E]     = XmlValueEnum.elementDecoder(self)
  implicit val attributeDecoder: AttributeDecoder[E] = XmlValueEnum.attributeDecoder(self)
  implicit val textDecoder: TextDecoder[E]           = XmlValueEnum.textDecoder(self)
}

trait LongXmlValueEnum[E <: LongEnumEntry] extends XmlValueEnum[Long, E] {
  self: LongEnum[E] =>
  implicit val elementEncoder: ElementEncoder[E]     = XmlValueEnum.elementEncoder[Long, E]
  implicit val attributeEncoder: AttributeEncoder[E] = XmlValueEnum.attributeEncoder[Long, E]
  implicit val textEncoder: TextEncoder[E]           = XmlValueEnum.textEncoder[Long, E]
  implicit val elementDecoder: ElementDecoder[E]     = XmlValueEnum.elementDecoder(self)
  implicit val attributeDecoder: AttributeDecoder[E] = XmlValueEnum.attributeDecoder(self)
  implicit val textDecoder: TextDecoder[E]           = XmlValueEnum.textDecoder(self)
}

trait ShortXmlValueEnum[E <: ShortEnumEntry] extends XmlValueEnum[Short, E] {
  self: ShortEnum[E] =>
  implicit val elementEncoder: ElementEncoder[E]     = XmlValueEnum.elementEncoder[Short, E]
  implicit val attributeEncoder: AttributeEncoder[E] = XmlValueEnum.attributeEncoder[Short, E]
  implicit val textEncoder: TextEncoder[E]           = XmlValueEnum.textEncoder[Short, E]
  implicit val elementDecoder: ElementDecoder[E]     = XmlValueEnum.elementDecoder(self)
  implicit val attributeDecoder: AttributeDecoder[E] = XmlValueEnum.attributeDecoder(self)
  implicit val textDecoder: TextDecoder[E]           = XmlValueEnum.textDecoder(self)
}

trait StringXmlValueEnum[E <: StringEnumEntry] extends XmlValueEnum[String, E] {
  self: StringEnum[E] =>
  implicit val elementEncoder: ElementEncoder[E]     = XmlValueEnum.elementEncoder[String, E]
  implicit val attributeEncoder: AttributeEncoder[E] = XmlValueEnum.attributeEncoder[String, E]
  implicit val textEncoder: TextEncoder[E]           = XmlValueEnum.textEncoder[String, E]
  implicit val elementDecoder: ElementDecoder[E]     = XmlValueEnum.elementDecoder(self)
  implicit val attributeDecoder: AttributeDecoder[E] = XmlValueEnum.attributeDecoder(self)
  implicit val textDecoder: TextDecoder[E]           = XmlValueEnum.textDecoder(self)
}

trait CharXmlValueEnum[E <: CharEnumEntry] extends XmlValueEnum[Char, E] {
  self: CharEnum[E] =>
  implicit val elementEncoder: ElementEncoder[E]     = XmlValueEnum.elementEncoder[Char, E]
  implicit val attributeEncoder: AttributeEncoder[E] = XmlValueEnum.attributeEncoder[Char, E]
  implicit val textEncoder: TextEncoder[E]           = XmlValueEnum.textEncoder[Char, E]
  implicit val elementDecoder: ElementDecoder[E]     = XmlValueEnum.elementDecoder(self)
  implicit val attributeDecoder: AttributeDecoder[E] = XmlValueEnum.attributeDecoder(self)
  implicit val textDecoder: TextDecoder[E]           = XmlValueEnum.textDecoder(self)
}

trait ByteXmlValueEnum[E <: ByteEnumEntry] extends XmlValueEnum[Byte, E] {
  self: ByteEnum[E] =>
  implicit val elementEncoder: ElementEncoder[E]     = XmlValueEnum.elementEncoder[Byte, E]
  implicit val attributeEncoder: AttributeEncoder[E] = XmlValueEnum.attributeEncoder[Byte, E]
  implicit val textEncoder: TextEncoder[E]           = XmlValueEnum.textEncoder[Byte, E]
  implicit val elementDecoder: ElementDecoder[E]     = XmlValueEnum.elementDecoder(self)
  implicit val attributeDecoder: AttributeDecoder[E] = XmlValueEnum.attributeDecoder(self)
  implicit val textDecoder: TextDecoder[E]           = XmlValueEnum.textDecoder(self)
}

object XmlValueEnum {

  def elementDecoder[V, E <: ValueEnumEntry[V]](
      e: ValueEnum[V, E],
  )(implicit baseDecoder: ElementDecoder[V]): ElementDecoder[E] =
    baseDecoder.emap(decodeFromValueType(e))

  def attributeDecoder[V, E <: ValueEnumEntry[V]](
      e: ValueEnum[V, E],
  )(implicit baseDecoder: AttributeDecoder[V]): AttributeDecoder[E] =
    baseDecoder.emap(decodeFromValueType(e))

  def textDecoder[V, E <: ValueEnumEntry[V]](
      e: ValueEnum[V, E],
  )(implicit baseDecoder: TextDecoder[V]): TextDecoder[E] =
    baseDecoder.emap(decodeFromValueType(e))

  def decodeFromValueType[V, E <: ValueEnumEntry[V]](
      e: ValueEnum[V, E],
  )(history: List[String], value: V): Either[DecodingError, E] =
    e.withValueOpt(value) match {
      case Some(member) => Right(member)
      case _            => Left(DecodingError(s"'$value' in not a member of enum $this", history, None))
    }

  def elementEncoder[V, E <: ValueEnumEntry[V]](
      implicit baseEncoder: ElementEncoder[V],
  ): ElementEncoder[E] = baseEncoder.contramap(_.value)

  def attributeEncoder[V, E <: ValueEnumEntry[V]](
      implicit baseEncoder: AttributeEncoder[V],
  ): AttributeEncoder[E] = baseEncoder.contramap(_.value)

  def textEncoder[V, E <: ValueEnumEntry[V]](
      implicit baseEncoder: TextEncoder[V],
  ): TextEncoder[E] = baseEncoder.contramap(_.value)

}
