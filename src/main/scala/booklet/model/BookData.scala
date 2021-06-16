package booklet.model

import upickle.default._

case class BookData(
    isbn: Isbn,
    author: Author,
    title: Title,
    subtitle: Option[Subtitle],
    thumbnail: Option[String],
    smallThumbnail: Option[String]
)

object BookData {
  implicit val writer: Writer[BookData] = macroW
}
