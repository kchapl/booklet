package booklet.services.book_finder

import booklet.Failure
import booklet.model.BookData
import booklet.services.book_finder.Model.GoogleBookResult
import booklet.utility.OptionPickler._
import okhttp3.{OkHttpClient, Request, Response}
import zio.{Has, UIO, ULayer, ZIO, ZManaged}

trait BookFinder {
  def findByIsbn(isbn: String): ZIO[Any, Failure, Option[BookData]]
}

object BookFinder {
  def findByIsbn(isbn: String): ZIO[Has[BookFinder], Failure, Option[BookData]] =
    ZIO.serviceWith(_.findByIsbn(isbn))
}

object BookFinderLive {

  private val client = new OkHttpClient()

  private def request(isbn: String) = new Request.Builder()
    .url(s"https://www.googleapis.com/books/v1/volumes?q=isbn:$isbn")
    .build()

  private def fetchResponse(isbn: String) =
    ZIO
      .effect(client.newCall(request(isbn)).execute())
      .mapError(Failure.fromThrowable)

  private def parse(response: Response) =
    ZIO
      .effect(read[GoogleBookResult](response.body.string))
      .mapBoth(Failure.fromThrowable, GoogleBookResult.toBook)

  private val effect: UIO[BookFinder] = UIO.succeed { isbn =>
    val fetchParsedResponse = for {
      response <- ZManaged.make(fetchResponse(isbn))(response => UIO.effectTotal(response.close()))
      book <- parse(response).toManaged_
    } yield book
    fetchParsedResponse.useNow
  }

  val layer: ULayer[Has[BookFinder]] = effect.toLayer
}
