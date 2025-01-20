package booklet.impure.router

import booklet.impure.service.BookHandler
import booklet.pure.Failure
import booklet.pure.http.CustomResponse.serverFailure
import zhttp.http.Method.{DELETE, GET, PATCH, POST}
import zhttp.http._

object BookRouter {
  val app: Http[BookHandler, Nothing, Request, Response] =
    Http
      .collectZIO[Request] {
        case req @ GET -> !! / "books"             => BookHandler.fetchAll(???)
        case req @ GET -> !! / "books" / bookId    => BookHandler.fetch(???, bookId)
        case req @ POST -> !! / "books"            => BookHandler.create(req, ???)
        case req @ PATCH -> !! / "books" / bookId  => BookHandler.update(bookId, req, ???)
        case req @ DELETE -> !! / "books" / bookId => BookHandler.delete(bookId, ???)
      }
      .foldHttp(
        failure => Http.succeed(serverFailure(failure)),
        defect => Http.succeed(serverFailure(Failure.fromThrowable(defect))),
        success => Http.succeed(success),
        Http.empty
      )
}
