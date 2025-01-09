package booklet.pure.model

import zio.json.{DeriveJsonEncoder, JsonEncoder}

case class BookData(
    isbn: Option[Isbn],
    author: Option[Author],
    title: Option[Title],
    subtitle: Option[Subtitle],
    thumbnail: Option[String],
    smallThumbnail: Option[String],
    userId: Option[String] // P5628
)

object BookData {
  implicit val encoder: JsonEncoder[BookData] = DeriveJsonEncoder.gen[BookData]

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
    smallThumbnail = None,
    userId = None // P5628
  )

  def partialFromHttpQuery(qry: Map[String, String]): BookData = BookData(
    isbn = qry.get(isbn).map(Isbn.apply),
    author = qry.get(authr).map(Author.apply),
    title = qry.get(ttle).map(Title.apply),
    subtitle = None,
    thumbnail = None,
    smallThumbnail = None,
    userId = None // P5628
  )
}
