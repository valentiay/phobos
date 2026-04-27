package phobos.refined.decoding

import scala.reflect.ClassTag

import phobos.decoding._

import eu.timepit.refined.api.{RefType, Validate}

trait DecodingInstances {

  implicit def refinedAttributeDecoder[F[_, _], T: ClassTag, P: ClassTag](
      implicit underlying: AttributeDecoder[T],
      refType: RefType[F],
      validate: Validate[T, P],
  ): AttributeDecoder[F[T, P]] =
    underlying.emap { (history, raw) =>
      refType.refine[P](raw) match {
        case Left(value)  => Left(mkDecodingError[T, P](raw, value, history))
        case Right(value) => Right(value)
      }
    }

  implicit def refinedTextDecoder[F[_, _], T: ClassTag, P: ClassTag](
      implicit underlying: TextDecoder[T],
      refType: RefType[F],
      validate: Validate[T, P],
  ): TextDecoder[F[T, P]] =
    underlying.emap { (history, raw) =>
      refType.refine[P](raw) match {
        case Left(value)  => Left(mkDecodingError[T, P](raw, value, history))
        case Right(value) => Right(value)
      }
    }

  implicit def refinedElementDecoder[F[_, _], T: ClassTag, P: ClassTag](
      implicit underlying: ElementDecoder[T],
      refType: RefType[F],
      validate: Validate[T, P],
  ): ElementDecoder[F[T, P]] =
    underlying.emap { (history, raw) =>
      refType.refine[P](raw) match {
        case Left(value)  => Left(mkDecodingError[T, P](raw, value, history))
        case Right(value) => Right(value)
      }
    }

  private def mkDecodingError[T: ClassTag, P: ClassTag](
      rawValue: T,
      error: String,
      history: List[String],
  ): DecodingError = {
    val T = humanReadableClassName(implicitly[ClassTag[T]].runtimeClass)
    val P = humanReadableClassName(implicitly[ClassTag[P]].runtimeClass)

    DecodingError(
      s"Failed to verify $P refinement for value=$rawValue of raw type $T: $error",
      history,
      None,
    )
  }

  // `string$MatchesRegex` -> `string.MatchesRegex`, `numeric$NonNegative$` -> `numeric.NonNegative`.
  private def humanReadableClassName(c: Class[_]): String =
    c.getName.replace('$', '.').stripSuffix(".")
}
