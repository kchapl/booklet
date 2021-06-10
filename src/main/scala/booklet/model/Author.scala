package booklet.model

import upickle.default._

case class Author(value: String) extends AnyVal

object Author {
  implicit val writer: Writer[Author] = macroW
}
