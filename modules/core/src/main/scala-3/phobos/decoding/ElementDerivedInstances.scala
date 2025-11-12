package phobos.decoding

import scala.deriving.Mirror

import phobos.configured.ElementCodecConfig
import phobos.derivation.decoder

private[decoding] trait ElementDerivedInstances {
  inline def derived[T]: ElementDecoder[T] =
    decoder.deriveElementDecoder[T](ElementCodecConfig.default)

  inline given [T <: reflect.Enum]: ElementDecoder[T] =
    decoder.deriveElementDecoder[T](ElementCodecConfig.default)
}
