package booklet.impure.router

import booklet.impure.service.ReadingHandler
import booklet.pure.Failure
import booklet.pure.http.CustomResponse.serverFailure
import zhttp.http.Method.{DELETE, GET, PATCH, POST}
import zhttp.http._

object ReadingRouter {
  val app: Http[ReadingHandler, Nothing, Request, Response] =
    Http
      .collectZIO[Request] {
        case req @ GET -> !! / "readings"             => ReadingHandler.fetchAll(???)
        case req @ GET -> !! / "readings" / readingId => ReadingHandler.fetch(readingId, ???)
        case req @ POST -> !! / "readings"            => ReadingHandler.create(req, ???)
        case req @ PATCH -> !! / "readings" / readingId =>
          ReadingHandler.update(readingId, req, ???)
        case req @ DELETE -> !! / "readings" / readingId => ReadingHandler.delete(readingId, ???)
      }
      .foldHttp(
        failure => Http.succeed(serverFailure(failure)),
        defect => Http.succeed(serverFailure(Failure.fromThrowable(defect))),
        success => Http.succeed(success),
        Http.empty
      )
}
