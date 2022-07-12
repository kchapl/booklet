package booklet.impure.router

import booklet.impure.service.bookfinder.GoogleBookFinder
import booklet.pure.http.CustomResponse.{badRequest, serverFailure}
import booklet.pure.http.Query
import zhttp.http.Method.GET
import zhttp.http._
import zio.ZIO

object BookFinderRouter {
  val app: Http[GoogleBookFinder, Nothing, Request, Response] =
    Http.collectZIO[Request] { case req @ GET -> !! / "find" =>
      Query.param(req)(name = "isbn") match {
        case None => ZIO.succeed(badRequest("Missing ISBN"))
        case Some(isbn) =>
          GoogleBookFinder
            .findByIsbn(isbn)
            .fold(
              serverFailure,
              {
                case None       => badRequest(s"No book found for ISBN $isbn")
                case Some(book) => Response.text(book.toString)
              }
            )
      }
    }
}
