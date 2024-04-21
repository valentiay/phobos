package phobos.encoding

import scala.deriving.Mirror

import phobos.configured.ElementCodecConfig
import phobos.derivation.auto.Auto
import phobos.derivation.{LazySummon, encoder}

private[encoding] trait ElementDerivedInstances {
  inline def derived[T]: ElementEncoder[T] =
    encoder.deriveElementEncoder[T](ElementCodecConfig.default)

  inline given [T](using mirror: Mirror.Of[T]): LazySummon[ElementEncoder, T] = new:
    def instance = encoder.deriveElementEncoder[T](ElementCodecConfig.default)
}
