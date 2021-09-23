package booklet.services.book_handler

import booklet.http.CustomResponse.{badRequest, notFound, ok, serverFailure}
import booklet.model.BookId
import booklet.services.database.Database
import booklet.views.BookView
import zhttp.http.UResponse
import zio.{Has, UIO, URLayer, ZIO}

object BookHandlerLive {

  val layer: URLayer[Has[Database], Has[BookHandler]] =
    ZIO.service[Database].map(toBookHandler).toLayer

  private def toBookHandler(db: Database): BookHandler =
    new BookHandler {
      val fetchAll: UIO[UResponse] =
        db.fetchAllBooks
          .fold(
            serverFailure,
            books => ok(BookView.list(books).toString)
          )

      def fetch(bookId: String): UIO[UResponse] =
        ZIO
          .fromOption(bookId.toLongOption)
          .map(BookId)
          .foldM(
            _ => ZIO.succeed(badRequest(s"Cannot parse ID $bookId")),
            id =>
              db
                .fetchBook(id)
                .fold(
                  serverFailure,
                  {
                    case None       => notFound(s"No such book: $bookId")
                    case Some(book) => ok(BookView.list(Seq(book)).toString)
                  }
                )
          )
    }
}
