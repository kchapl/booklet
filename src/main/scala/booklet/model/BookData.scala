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

  def fromHttpQuery(qry: Map[String, String]): Option[BookData] = for {
    isbn <- qry.get("isbn")
    author <- qry.get("author")
    title <- qry.get("title")
  } yield BookData(
    Isbn(isbn),
    Author(author),
    Title(title),
    None,
    None,
    None
  )
}
