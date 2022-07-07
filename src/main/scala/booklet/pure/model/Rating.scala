package booklet.pure.model

import zio.json.{DeriveJsonEncoder, JsonEncoder}

case class Rating(value: Int) extends AnyVal

object Rating {
  implicit val encoder: JsonEncoder[Rating] = DeriveJsonEncoder.gen[Rating]
}
