package phobos.derivation.auto

import phobos.decoding.ElementDecoder

private[phobos] trait ElementDecoderAutoInstances {
  implicit def autoDecoder[A](implicit auto: Auto[ElementDecoder[A]]): ElementDecoder[A] = auto.value
}
