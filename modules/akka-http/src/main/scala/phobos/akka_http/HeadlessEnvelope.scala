package phobos.akka_http

import phobos.decoding.{ElementDecoder, XmlDecoder}
import phobos.derivation.semiauto._
import phobos.encoding.{ElementEncoder, XmlEncoder}
import phobos.syntax.xmlns

final case class HeadlessEnvelope[Body](@xmlns(soapenv) Body: Body)

object HeadlessEnvelope {
  implicit def deriveEnvelopeEncoder[Body: ElementEncoder]: XmlEncoder[HeadlessEnvelope[Body]] = {
    implicit val envelopeElementEncoder: ElementEncoder[HeadlessEnvelope[Body]] =
      deriveElementEncoder[HeadlessEnvelope[Body]]

    XmlEncoder.fromElementEncoderNs[HeadlessEnvelope[Body], soapenv]("Envelope")
  }

  implicit def deriveEnvelopeDecoder[Body: ElementDecoder]: XmlDecoder[HeadlessEnvelope[Body]] = {
    implicit val envelopeElementDecoder: ElementDecoder[HeadlessEnvelope[Body]] =
      deriveElementDecoder[HeadlessEnvelope[Body]]

    XmlDecoder.fromElementDecoderNs[HeadlessEnvelope[Body], soapenv]("Envelope")
  }
}
