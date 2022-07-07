package booklet.pure.model

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

  private val isbn = "isbn"
  private val authr = "author"
  private val ttle = "title"

  def completeFromHttpQuery(qry: Map[String, String]): Option[BookData] = for {
    isbn <- qry.get(isbn)
    author <- qry.get(authr)
    title <- qry.get(ttle)
  } yield BookData(
    isbn = Some(Isbn(isbn)),
    author = Some(Author(author)),
    title = Some(Title(title)),
    subtitle = None,
    thumbnail = None,
    smallThumbnail = None
  )

  def partialFromHttpQuery(qry: Map[String, String]): BookData = BookData(
    isbn = qry.get(isbn).map(Isbn.apply),
    author = qry.get(authr).map(Author.apply),
    title = qry.get(ttle).map(Title.apply),
    subtitle = None,
    thumbnail = None,
    smallThumbnail = None
  )
}
