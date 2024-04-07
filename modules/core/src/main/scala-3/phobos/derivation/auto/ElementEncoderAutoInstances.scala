package phobos.derivation.auto

import phobos.encoding.ElementEncoder

private[phobos] trait ElementEncoderAutoInstances {
  implicit def autoEncoder[A](implicit auto: Auto[ElementEncoder[A]]): ElementEncoder[A] = auto.value
}
