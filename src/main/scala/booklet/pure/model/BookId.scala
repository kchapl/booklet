package booklet.pure.model

import upickle.default.{macroW, Writer}

case class BookId(value: Long) extends AnyVal

object BookId {
  implicit val writer: Writer[BookId] = macroW
}
