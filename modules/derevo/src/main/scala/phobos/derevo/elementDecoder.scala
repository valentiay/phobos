package phobos.derevo

import derevo.{Derevo, Derivation, delegating}
import phobos.decoding.ElementDecoder

@delegating("phobos.derivation.semiauto.deriveElementDecoder")
object elementDecoder extends Derivation[ElementDecoder] {
  implicit def instance[T]: ElementDecoder[T] = macro Derevo.delegate[ElementDecoder, T]
}
