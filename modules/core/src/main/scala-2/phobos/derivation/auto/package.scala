package phobos.derivation

import phobos.encoding.ElementEncoder
import phobos.decoding.ElementDecoder

/** Importing contents of this package will provide ElementEncoder / ElementDecoder instances for any case class. It may
  * be useful while researching, however do not use this in production, because automatic derivation is less performant
  * than semiautomatic. It also has some issues when deriving codecs in complicated cases.
  */
package object auto {
  implicit def deriveAutoEncoder[A]: Auto[ElementEncoder[A]] = macro AutoMacro.autoEncoder[A]
  // Would not work for scala 2 if placed in ElementEncoderAutoInstances, unlike scala 3
  implicit def autoEncoder[A](implicit auto: Auto[ElementEncoder[A]]): ElementEncoder[A] = auto.value

  implicit def deriveAutoDecoder[A]: Auto[ElementDecoder[A]] = macro AutoMacro.autoDecoder[A]
  // Would not work for scala 2 if placed in ElementDecoderAutoInstances, unlike scala 3
  implicit def autoDecoder[A](implicit auto: Auto[ElementDecoder[A]]): ElementDecoder[A] = auto.value
}
