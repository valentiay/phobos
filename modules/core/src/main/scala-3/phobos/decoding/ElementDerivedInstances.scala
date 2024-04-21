package phobos.decoding

import scala.deriving.Mirror

import phobos.configured.ElementCodecConfig
import phobos.derivation.LazySummon
import phobos.derivation.decoder

private[decoding] trait ElementDerivedInstances {
  inline def derived[T]: ElementDecoder[T] =
    decoder.deriveElementDecoder[T](ElementCodecConfig.default)

  inline given [T](using mirror: Mirror.Of[T]): LazySummon[ElementDecoder, T] = new:
    def instance = decoder.deriveElementDecoder[T](ElementCodecConfig.default)
}
