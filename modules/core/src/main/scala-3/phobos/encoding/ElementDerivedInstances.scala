package phobos.encoding

import scala.deriving.Mirror

import phobos.configured.ElementCodecConfig
import phobos.derivation.auto.Auto
import phobos.derivation.encoder

private[encoding] trait ElementDerivedInstances {
  inline def derived[T]: ElementEncoder[T] =
    encoder.deriveElementEncoder[T](ElementCodecConfig.default)

  inline given [T <: reflect.Enum]: ElementEncoder[T] =
    encoder.deriveElementEncoder[T](ElementCodecConfig.default)
}
