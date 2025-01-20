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
        case req @ GET -> !! / "readings"                => ReadingHandler.fetchAll(req)
        case req @ GET -> !! / "readings" / readingId    => ReadingHandler.fetch(req, readingId)
        case req @ POST -> !! / "readings"               => ReadingHandler.create(req)
        case req @ PATCH -> !! / "readings" / readingId  => ReadingHandler.update(req, readingId)
        case req @ DELETE -> !! / "readings" / readingId => ReadingHandler.delete(req, readingId)
      }
      .foldHttp(
        failure => Http.succeed(serverFailure(failure)),
        defect => Http.succeed(serverFailure(Failure.fromThrowable(defect))),
        success => Http.succeed(success),
        Http.empty
      )
}
