package booklet.services.book_finder

import booklet.Failure
import booklet.model.BookData
import booklet.services.book_finder.Model.GoogleBookResult
import okhttp3.{OkHttpClient, Request}
import upickle.legacy.read
import zio.{Has, UIO, ULayer, ZIO}

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

  private val effect: UIO[BookFinder] = UIO.succeed(isbn =>
    for {
      // TODO close response
      response <- ZIO
        .effect(client.newCall(request(isbn)).execute())
        .mapError(Failure.fromThrowable)
      responseBody = response.body.string
      result <-
        ZIO.effect(read[GoogleBookResult](responseBody)).mapError(Failure.fromThrowable)
      book = GoogleBookResult.toBook(result)
    } yield book
  )

  val layer: ULayer[Has[BookFinder]] = effect.toLayer
}
