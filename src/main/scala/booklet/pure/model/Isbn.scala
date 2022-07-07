package booklet.pure.model

import upickle.default._

case class Isbn(value: String) extends AnyVal

object Isbn {
  implicit val writer: Writer[Isbn] = macroW
}
