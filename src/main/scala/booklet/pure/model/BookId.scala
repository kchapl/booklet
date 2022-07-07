package booklet.pure.model

import zio.json.{DeriveJsonEncoder, JsonEncoder}

case class BookId(value: Long) extends AnyVal

object BookId {
  implicit val encoder: JsonEncoder[BookId] = DeriveJsonEncoder.gen[BookId]
}
