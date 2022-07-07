package booklet.pure.model

import zio.json.{DeriveJsonEncoder, JsonEncoder}

case class Isbn(value: String) extends AnyVal

object Isbn {
  implicit val encoder: JsonEncoder[Isbn] = DeriveJsonEncoder.gen[Isbn]
}
