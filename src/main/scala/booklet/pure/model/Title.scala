package booklet.pure.model

import zio.json.{DeriveJsonEncoder, JsonEncoder}

case class Title(value: String) extends AnyVal

object Title {
  implicit val encoder: JsonEncoder[Title] = DeriveJsonEncoder.gen[Title]
}
