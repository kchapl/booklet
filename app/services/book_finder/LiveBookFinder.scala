package services.book_finder

import model.Book
import okhttp3._
import upickle.default.{read, _}
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
    def toBook(result: GoogleBookResult): Option[Book] =
      if (result.totalItems == 1)
        for {
          item   <- result.items.headOption
          author <- item.volumeInfo.authors.headOption
        } yield Book(author, title = item.volumeInfo.title)
      else None
  }

  val impl: ZLayer[Any, Throwable, BookFinder] = {
    val client = new OkHttpClient()
    ZLayer.succeed { isbn =>
      val request = new Request.Builder()
        .url(s"https://www.googleapis.com/books/v1/volumes?q=isbn:$isbn")
        .build()
      val response = client.newCall(request).execute()
      val b        = response.body.string
      val x        =
        //      val googleBookResult = read[GoogleBookResult](response.body.string)
        ZIO.effect(read[GoogleBookResult](b)).map(GoogleBookResult.toBook)
      println(b)
      x
    }
  }
}
