package booklet.model

import upickle.default._

case class BookToInsert(
    isbn: Isbn,
    author: Author,
    title: Title,
    subtitle: Option[Subtitle],
    thumbnail: Option[String],
    smallThumbnail: Option[String]
)

object BookToInsert {
  implicit val writer: Writer[BookToInsert] = macroW
}
