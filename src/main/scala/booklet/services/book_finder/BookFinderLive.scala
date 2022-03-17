package booklet.services.book_finder

import booklet.Failure
import booklet.services.book_finder.Model.GoogleBookResult
import booklet.utility.OptionPickler._
import okhttp3.{OkHttpClient, Request, Response}
import zio._

object BookFinderLive {

  private val client = new OkHttpClient()

  private def request(isbn: String) = new Request.Builder()
    .url(s"https://www.googleapis.com/books/v1/volumes?q=isbn:$isbn")
    .build()

  private def fetchResponse(isbn: String) =
    ZIO
      .attempt(client.newCall(request(isbn)).execute())
      .mapError(Failure.fromThrowable)

  private def parse(response: Response) =
    ZIO
      .attempt(read[GoogleBookResult](response.body.string))
      .mapBoth(Failure.fromThrowable, GoogleBookResult.toBook)

  private val effect: UIO[BookFinder] = ZIO.succeed { isbn =>
    val fetchParsedResponse = for {
      response <- ZManaged.acquireReleaseWith(fetchResponse(isbn))(response =>
        UIO.succeed(response.close())
      )
      book <- parse(response).toManaged
    } yield book
    fetchParsedResponse.useNow
  }

  val layer: Layer[Failure, BookFinder] = effect.toLayer
}
