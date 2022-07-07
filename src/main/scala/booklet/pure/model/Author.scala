package booklet.pure.model

import zio.json.{DeriveJsonEncoder, JsonEncoder}

case class Author(value: String) extends AnyVal

object Author {
  implicit val encoder: JsonEncoder[Author] = DeriveJsonEncoder.gen[Author]
}
