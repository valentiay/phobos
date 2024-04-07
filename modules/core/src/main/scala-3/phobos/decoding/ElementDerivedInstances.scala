package phobos.decoding

import phobos.configured.ElementCodecConfig
import phobos.derivation.decoder
import phobos.derivation.LazySummon
import scala.deriving.Mirror

private[decoding] trait ElementDerivedInstances {
  inline def derived[T]: ElementDecoder[T] =
    decoder.deriveElementDecoder[T](ElementCodecConfig.default)

  inline given [T](using mirror: Mirror.Of[T]): LazySummon[ElementDecoder, T] = new:
    def instance = decoder.deriveElementDecoder[T](ElementCodecConfig.default)
}
