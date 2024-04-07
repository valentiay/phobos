package phobos.derivation

import phobos.decoding.ElementDecoder
import phobos.encoding.ElementEncoder

package object auto {
  inline implicit def deriveAutoEncoder[A]: Auto[ElementEncoder[A]] =
    Auto(semiauto.deriveElementEncoder[A])

  inline implicit def deriveAutoDecoder[A]: Auto[ElementDecoder[A]] =
    Auto(semiauto.deriveElementDecoder[A])
}
