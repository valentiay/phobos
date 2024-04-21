package phobos

import scala.util.{Failure, Success, Try}

import com.fasterxml.aalto.{AsyncByteArrayFeeder, AsyncXMLStreamReader}

package object decoding {
  type XmlStreamReader = AsyncXMLStreamReader[AsyncByteArrayFeeder]

  private[decoding] def wrapException[A](f: String => A) =
    (history: List[String], string: String) =>
      Try(f(string)) match {
        case Failure(exception) =>
          Left(DecodingError(Option(exception.getMessage).getOrElse("No text provided"), history, Some(exception)))
        case Success(a) => Right(a)
      }
}
