package booklet.pure.model

import upickle.default._

case class Title(value: String) extends AnyVal

object Title {
  implicit val writer: Writer[Title] = macroW
}
