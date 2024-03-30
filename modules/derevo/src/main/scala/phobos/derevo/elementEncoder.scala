package phobos.derevo

import derevo.{Derevo, Derivation, delegating}
import phobos.encoding.ElementEncoder

@delegating("phobos.derivation.semiauto.deriveElementEncoder")
object elementEncoder extends Derivation[ElementEncoder] {
  implicit def instance[T]: ElementEncoder[T] = macro Derevo.delegate[ElementEncoder, T]
}
