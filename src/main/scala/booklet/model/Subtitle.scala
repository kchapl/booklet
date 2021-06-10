package booklet.model

import upickle.default._

case class Subtitle(value: String) extends AnyVal

object Subtitle {
  implicit val writer: Writer[Subtitle] = macroW
}
