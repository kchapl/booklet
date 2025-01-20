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
        case req @ GET -> !! / "books"             => BookHandler.fetchAll(req)
        case req @ GET -> !! / "books" / bookId    => BookHandler.fetch(req, bookId)
        case req @ POST -> !! / "books"            => BookHandler.create(req)
        case req @ PATCH -> !! / "books" / bookId  => BookHandler.update(req, bookId)
        case req @ DELETE -> !! / "books" / bookId => BookHandler.delete(req, bookId)
      }
      .foldHttp(
        failure => Http.succeed(serverFailure(failure)),
        defect => Http.succeed(serverFailure(Failure.fromThrowable(defect))),
        success => Http.succeed(success),
        Http.empty
      )
}
