package services.book_finder

import config.Config.config
import model.{Author, BookLookupFailure, BookToInsert, ISBN, Title}
import okhttp3._
import services.book_finder.OptionPickler._
import zio._

object LiveBookFinder {

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
      publisher: String,
      publishedDate: String,
      description: String,
      industryIdentifiers: Seq[Identifier],
      categories: Seq[String],
      imageLinks: ImageLinks
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
    def toBook(result: GoogleBookResult): Option[BookToInsert] =
      if (result.totalItems == 1)
        for {
          item <- result.items.headOption
          info = item.volumeInfo
          author <- info.authors.headOption
        } yield BookToInsert(
          ISBN(
            info.industryIdentifiers
              .find(_.`type` == "ISBN_13")
              .map(_.identifier)
              .getOrElse("Unknown")
          ),
          Author(author),
          Title(info.title),
          None,
          thumbnail = Some(info.imageLinks.thumbnail),
          smallThumbnail = Some(info.imageLinks.smallThumbnail)
        )
      else None
  }

  case class EmptyGoogleBookResult(totalItems: Int, kind: String)
  object EmptyGoogleBookResult {
    implicit val reader: Reader[EmptyGoogleBookResult] = macroR
  }

  val impl: ZLayer[Any, Nothing, BookFinder] = {
    val client = new OkHttpClient()
    ZLayer.succeed { isbn =>
      val request = new Request.Builder()
        .url(s"${config.bookLookupUrl}?key=${config.bookLookupKey}&q=isbn:$isbn")
        .build()
      val response = client.newCall(request).execute()
      val body     = response.body.string
      ZIO
        .effect(read[GoogleBookResult](body))
        .orElse(ZIO.effect(read[EmptyGoogleBookResult](body)))
        .bimap(
          e => BookLookupFailure(s"Could not parse:\n$body\n$e"),
          {
            case result: GoogleBookResult => GoogleBookResult.toBook(result)
            case _: EmptyGoogleBookResult => None
          }
        )
    }
  }
}
// TODO remove
// {
//  "kind": "books#volumes",
//  "totalItems": 0
//}
