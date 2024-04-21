package phobos.derevo

import phobos.encoding.ElementEncoder

import derevo.{Derevo, Derivation, delegating}

@delegating("phobos.derivation.semiauto.deriveElementEncoder")
object elementEncoder extends Derivation[ElementEncoder] {
  implicit def instance[T]: ElementEncoder[T] = macro Derevo.delegate[ElementEncoder, T]
}
