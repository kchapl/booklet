package booklet.pure.model

import upickle.default._

case class Rating(value: Int) extends AnyVal

object Rating {
  implicit val writer: Writer[Rating] = macroW
}
