package booklet.services.book_handler

import booklet.Failure
import booklet.http.CustomResponse
import booklet.http.CustomResponse.toContent
import booklet.model.BookId
import booklet.services.database.Database
import booklet.views.BookView
import zhttp.http.Status.{BAD_REQUEST, INTERNAL_SERVER_ERROR, NOT_FOUND}
import zhttp.http.{Response, UResponse}
import zio.{Has, RIO, UIO, URLayer, ZIO}

trait BookHandler {
  def fetchAll: UIO[UResponse]

  def fetch(bookId: String): UIO[UResponse]
}

object BookHandler {
  val fetchAll: RIO[Has[BookHandler], UResponse] = RIO.serviceWith(_.fetchAll)

  def fetch(bookId: String): RIO[Has[BookHandler], UResponse] = RIO.serviceWith(_.fetch(bookId))

  val live: URLayer[Has[Database], Has[BookHandler]] = {

    def serverFailureResponse(failure: Failure) =
      Response.http(
        status = INTERNAL_SERVER_ERROR,
        content = toContent(failure.cause.toString)
      )

    ZIO
      .service[Database]
      .map { database =>
        new BookHandler {
          val fetchAll: UIO[UResponse] =
            database.fetchAllBooks
              .fold(
                serverFailureResponse,
                books => CustomResponse.htmlString(BookView.list(books).toString)
              )

          def fetch(bookId: String): UIO[UResponse] =
            ZIO
              .fromOption(bookId.toLongOption)
              .map(BookId)
              .foldM(
                _ =>
                  ZIO.succeed(
                    Response.http(
                      status = BAD_REQUEST,
                      content = toContent(s"Cannot parse ID $bookId")
                    )
                  ),
                id =>
                  database
                    .fetchBook(id)
                    .fold(
                      serverFailureResponse,
                      {
                        case None =>
                          Response.http(
                            status = NOT_FOUND,
                            content = toContent(s"No such book: $bookId")
                          )
                        case Some(book) =>
                          CustomResponse.htmlString(BookView.list(Seq(book)).toString)
                      }
                    )
              )
        }
      }
      .toLayer
  }
}
