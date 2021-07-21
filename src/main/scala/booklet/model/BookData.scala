package booklet.model

import upickle.default._

case class BookData(
    isbn: Option[Isbn],
    author: Option[Author],
    title: Option[Title],
    subtitle: Option[Subtitle],
    thumbnail: Option[String],
    smallThumbnail: Option[String]
)

object BookData {
  implicit val writer: Writer[BookData] = macroW

  def completeFromHttpQuery(qry: Map[String, String]): Option[BookData] = for {
    isbn <- qry.get("isbn")
    author <- qry.get("author")
    title <- qry.get("title")
  } yield BookData(
    Some(Isbn(isbn)),
    Some(Author(author)),
    Some(Title(title)),
    None,
    None,
    None
  )

  def partialFromHttpQuery(qry: Map[String, String]): BookData = BookData(
    qry.get("isbn").map(Isbn(_)),
    qry.get("author").map(Author(_)),
    qry.get("title").map(Title(_)),
    None,
    None,
    None
  )
}
