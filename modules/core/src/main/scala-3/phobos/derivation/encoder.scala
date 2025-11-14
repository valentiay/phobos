package phobos.derivation

import scala.annotation.nowarn
import scala.compiletime.*
import scala.deriving.Mirror
import scala.quoted.*

import phobos.Namespace
import phobos.configured.ElementCodecConfig
import phobos.derivation.common.*
import phobos.encoding.*
import phobos.syntax.*

@nowarn("msg=Use errorAndAbort")
object encoder {

  inline def deriveElementEncoder[T](
      inline config: ElementCodecConfig,
  ): ElementEncoder[T] =
    summonFrom {
      case _: Mirror.ProductOf[T] => deriveProduct(config)
      case m: Mirror.SumOf[T]     => deriveSum(config, m)
      case _                      => error(s"${showType[T]} is not a sum type or product type")
    }

  inline def deriveXmlEncoder[T](
      inline localName: String,
      inline namespace: Option[String],
      inline preferredNamespacePrefix: Option[String],
      inline config: ElementCodecConfig,
  ): XmlEncoder[T] =
    XmlEncoder.fromElementEncoder[T](localName, namespace, preferredNamespacePrefix)(deriveElementEncoder(config))

  // PRODUCT

  private def encodeAttributes[T: Type](using Quotes)(
      fields: List[ProductTypeField],
      sw: Expr[PhobosStreamWriter],
      a: Expr[T],
  ): Expr[List[Unit]] = {
    import quotes.reflect.*
    val classTypeRepr = TypeRepr.of[T]
    val classSymbol   = classTypeRepr.typeSymbol
    Expr.ofList(fields.map { field =>
      field.typeRepr.asType match {
        case '[t] =>
          '{
            summonInline[AttributeEncoder[t]].encodeAsAttribute(
              ${ Select(a.asTerm, classSymbol.declaredField(field.localName)).asExprOf[t] },
              $sw,
              ${ field.xmlName },
              ${ field.namespace }.map(_.getNamespace),
            )
          }
      }
    })
  }

  private def encodeText[T: Type](using Quotes)(
      fields: List[ProductTypeField],
      sw: Expr[PhobosStreamWriter],
      a: Expr[T],
  ): Expr[List[Unit]] = {
    import quotes.reflect.*
    val classTypeRepr = TypeRepr.of[T]
    val classSymbol   = classTypeRepr.typeSymbol

    Expr.ofList(fields.map { field =>
      field.typeRepr.asType match {
        case '[t] =>
          '{
            summonInline[TextEncoder[t]]
              .encodeAsText(${ Select(a.asTerm, classSymbol.declaredField(field.localName)).asExprOf[t] }, $sw)
          }
      }
    })
  }

  private def encodeElements[T: Type](using Quotes)(
      fields: List[ProductTypeField],
      sw: Expr[PhobosStreamWriter],
      a: Expr[T],
  ): Expr[List[Unit]] = {
    import quotes.reflect.*
    val classTypeRepr = TypeRepr.of[T]
    val classSymbol   = classTypeRepr.typeSymbol

    Expr.ofList(fields.map { field =>
      field.typeRepr.asType match {
        case '[t] =>
          '{
            summonInline[ElementEncoder[t]].encodeAsElement(
              ${ Select(a.asTerm, classSymbol.declaredField(field.localName)).asExprOf[t] },
              $sw,
              ${ field.xmlName },
              ${ field.namespace }.map(_.getNamespace),
              ${ field.namespace }.flatMap(_.getPreferredPrefix),
            )
          }
      }
    })
  }

  inline def deriveProduct[T](inline config: ElementCodecConfig): ElementEncoder[T] =
    ${ deriveProductImpl[T]('config) }

  private def deriveProductImpl[T: Type](config: Expr[ElementCodecConfig])(using Quotes): Expr[ElementEncoder[T]] = {
    val fields = extractProductTypeFields[T](config)
    val groups = fields.groupBy(_.category)

    '{
      new ElementEncoder[T] {
        def encodeAsElement(
            a: T,
            sw: PhobosStreamWriter,
            localName: String,
            namespaceUri: Option[String],
            preferredNamespacePrefix: Option[String],
        ): Unit = {
          namespaceUri.fold(sw.writeStartElement(localName))(ns =>
            sw.writeStartElement(preferredNamespacePrefix.orNull, localName, ns),
          )
          $config.scopeDefaultNamespace.foreach { uri =>
            sw.writeAttribute("xmlns", uri)
          }
          $config.defineNamespaces.foreach {
            case (uri, Some(prefix)) =>
              if (sw.getNamespaceContext.getPrefix(uri) == null) sw.writeNamespace(prefix, uri)
            case (uri, None) =>
              if (sw.getNamespaceContext.getPrefix(uri) == null) sw.writeNamespace(uri)
          }

          ${ encodeAttributes[T](groups.getOrElse(FieldCategory.attribute, Nil), 'sw, 'a) }
          ${ encodeText[T](groups.getOrElse(FieldCategory.text, Nil), 'sw, 'a) }
          ${
            encodeElements[T](
              (groups.getOrElse(FieldCategory.element, Nil) ::: groups.getOrElse(FieldCategory.default, Nil)),
              'sw,
              'a,
            )
          }

          sw.writeEndElement()
        }
      }
    }
  }

  // SUM

  inline def deriveSum[T](
      inline config: ElementCodecConfig,
      m: Mirror.SumOf[T],
  ): ElementEncoder[T] = {
    type Children = m.MirroredElemTypes

    val childEncoders =
      inline if (isEnum[T]) autoDeriveEnumChildren[Children, T]
      else summonAll[Tuple.Map[Children, [t] =>> ElementEncoder[t]]].toList.asInstanceOf[List[ElementEncoder[T]]]

    val xmlNames   = extractSumXmlNames[T](config)
    val childInfos = xmlNames.zip(childEncoders).map(SumTypeChild(_, _))

    new ElementEncoder[T] {
      def encodeAsElement(
          t: T,
          sw: PhobosStreamWriter,
          localName: String,
          namespaceUri: Option[String],
          preferredNamespacePrefix: Option[String],
      ): Unit = {
        val childInfo = childInfos(m.ordinal(t))
        val discr =
          if (config.useElementNameAsDiscriminator) childInfo.xmlName
          else {
            sw.memorizeDiscriminator(
              config.discriminatorNamespace,
              config.discriminatorLocalName,
              childInfo.xmlName,
            )
            localName
          }

        childInfo.tc.encodeAsElement(t, sw, discr, namespaceUri, preferredNamespacePrefix)
      }
    }
  }

  private inline def autoDeriveEnumChildren[T <: Tuple, Base]: List[ElementEncoder[Base]] = {
    inline erasedValue[T] match {
      case _: EmptyTuple => Nil
      case _: (t *: ts) =>
        deriveElementEncoder[t](ElementCodecConfig.default).asInstanceOf[ElementEncoder[Base]] ::
          autoDeriveEnumChildren[ts, Base]
    }
  }
}
