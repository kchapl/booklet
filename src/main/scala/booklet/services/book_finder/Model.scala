package booklet.services.book_finder

import booklet.model.{Author, BookData, Isbn, Title}
import booklet.utility.OptionPickler._
import cats.implicits._

object Model {

  case class Identifier(`type`: String, identifier: String)

  object Identifier {
    implicit val reader: Reader[Identifier] = macroR
  }

  case class ImageLinks(smallThumbnail: String, thumbnail: String)

  object ImageLinks {
    implicit val reader: Reader[ImageLinks] = macroR
  }

  case class GoogleBook(
      title: String,
      subtitle: Option[String] = None,
      authors: Seq[String],
      publisher: Option[String] = None,
      publishedDate: String,
      description: Option[String] = None,
      industryIdentifiers: Seq[Identifier],
      categories: Seq[String],
      imageLinks: Option[ImageLinks] = None
  )

  object GoogleBook {
    implicit val reader: Reader[GoogleBook] = macroR
  }

  case class GoogleBookItem(volumeInfo: GoogleBook)

  object GoogleBookItem {
    implicit val reader: Reader[GoogleBookItem] = macroR
  }

  case class GoogleBookResult(totalItems: Int, items: Seq[GoogleBookItem])

  object GoogleBookResult {
    implicit val reader: Reader[GoogleBookResult] = macroR

    def toBook(result: GoogleBookResult): Option[BookData] =
      if (result.totalItems === 1)
        for {
          item <- result.items.headOption
          info = item.volumeInfo
          author <- info.authors.headOption
        } yield BookData(
          Some(
            Isbn(
              info.industryIdentifiers
                .find(_.`type` === "ISBN_13")
                .map(_.identifier)
                .getOrElse("Unknown")
            )
          ),
          Some(Author(author)),
          Some(Title(info.title)),
          None,
          thumbnail = info.imageLinks.map(_.thumbnail),
          smallThumbnail = info.imageLinks.map(_.smallThumbnail)
        )
      else None
  }

  case class EmptyGoogleBookResult(totalItems: Int, kind: String)

  object EmptyGoogleBookResult {
    implicit val reader: Reader[EmptyGoogleBookResult] = macroR
  }
}
