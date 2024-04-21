package phobos.derevo

import phobos.decoding.ElementDecoder

import derevo.{Derevo, Derivation, delegating}

@delegating("phobos.derivation.semiauto.deriveElementDecoder")
object elementDecoder extends Derivation[ElementDecoder] {
  implicit def instance[T]: ElementDecoder[T] = macro Derevo.delegate[ElementDecoder, T]
}
