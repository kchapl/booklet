package booklet.impure.service.bookfinder

import booklet.pure.model.{Author, BookData, Isbn, Title}
import cats.implicits._
import zio.json.{DeriveJsonDecoder, JsonDecoder}

object GoogleBookModel {

  case class Identifier(`type`: String, identifier: String)

  object Identifier {
    implicit val decoder: JsonDecoder[Identifier] = DeriveJsonDecoder.gen[Identifier]
  }

  case class ImageLinks(smallThumbnail: String, thumbnail: String)

  object ImageLinks {
    implicit val decoder: JsonDecoder[ImageLinks] = DeriveJsonDecoder.gen[ImageLinks]
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
    implicit val decoder: JsonDecoder[GoogleBook] = DeriveJsonDecoder.gen[GoogleBook]
  }

  case class GoogleBookItem(volumeInfo: GoogleBook)

  object GoogleBookItem {
    implicit val decoder: JsonDecoder[GoogleBookItem] = DeriveJsonDecoder.gen[GoogleBookItem]
  }

  case class GoogleBookResult(totalItems: Int, items: Seq[GoogleBookItem])

  object GoogleBookResult {
    implicit val decoder: JsonDecoder[GoogleBookResult] = DeriveJsonDecoder.gen[GoogleBookResult]

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
          smallThumbnail = info.imageLinks.map(_.smallThumbnail),
          ???
        )
      else None
  }

  case class EmptyGoogleBookResult(totalItems: Int, kind: String)

  object EmptyGoogleBookResult {
    implicit val decoder: JsonDecoder[EmptyGoogleBookResult] =
      DeriveJsonDecoder.gen[EmptyGoogleBookResult]
  }
}
