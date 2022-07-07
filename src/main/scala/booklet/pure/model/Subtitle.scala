package booklet.pure.model

import zio.json.{DeriveJsonEncoder, JsonEncoder}

case class Subtitle(value: String) extends AnyVal

object Subtitle {
  implicit val encoder: JsonEncoder[Subtitle] = DeriveJsonEncoder.gen[Subtitle]
}
