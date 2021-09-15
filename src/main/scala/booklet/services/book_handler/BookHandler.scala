package booklet.services.book_handler

import booklet.http.CustomResponse
import booklet.http.CustomResponse.toContent
import booklet.services.database.Database
import booklet.views.BookView
import zhttp.http.Status.INTERNAL_SERVER_ERROR
import zhttp.http.{Response, UResponse}
import zio.{Has, RIO, UIO, URLayer, ZIO}

trait BookHandler {
  def fetchAll: UIO[UResponse]
}

object BookHandler {
  val fetchAll: RIO[Has[BookHandler], UResponse] = RIO.serviceWith(_.fetchAll)

  val live: URLayer[Has[Database], Has[BookHandler]] =
    ZIO
      .service[Database]
      .map { database =>
        new BookHandler {
          val fetchAll: UIO[UResponse] =
            database.fetchAllBooks
              .fold(
                failure =>
                  Response.http(
                    status = INTERNAL_SERVER_ERROR,
                    content = toContent(failure.message)
                  ),
                books => CustomResponse.htmlString(BookView.list(books).toString)
              )
        }
      }
      .toLayer
}
