package booklet.services.book_handler

import booklet.Failure
import booklet.http.CustomResponse._
import booklet.http.Query
import booklet.model.{BookData, BookId}
import booklet.services.database.Database
import booklet.views.BookView
import zhttp.http._
import zio.{Has, IO, UIO, URIO, URLayer, ZIO}

object BookHandlerLive {

  private def toBookId(bookId: String) =
    ZIO
      .fromOption(bookId.toLongOption)
      .map(BookId(_))

  private def fromDatabase(db: Database) =
    new BookHandler {

      def fetchAll: UIO[Response] =
        db.fetchAllBooks
          .fold(
            serverFailure,
            books => ok(BookView.list(books).toString)
          )

      def fetch(bookId: String): UIO[Response] =
        toBookId(bookId)
          .foldM(
            _ => ZIO.succeed(badRequest(s"Cannot parse ID $bookId")),
            id =>
              db
                .fetchBook(id)
                .fold(
                  serverFailure,
                  {
                    case None       => notFound(Path(bookId))
                    case Some(book) => ok(BookView.list(Seq(book)).toString)
                  }
                )
          )

      def create(request: Request): IO[Failure, Response] =
        Query
          .fromRequest(request)
          .flatMap(requestQry =>
            ZIO
              .fromOption(BookData.completeFromHttpQuery(requestQry))
              .foldM(
                _ => ZIO.succeed(badRequest(requestQry.toString)),
                bookData =>
                  db
                    .insertBook(bookData)
                    .fold(
                      serverFailure,
                      _ => seeOther(path = "/books")
                    )
              )
          )

      def update(bookId: String)(request: Request): IO[Failure, Response] =
        Query
          .fromRequest(request)
          .flatMap(requestQry =>
            toBookId(bookId)
              .foldM(
                _ => ZIO.succeed(badRequest(requestQry.toString)),
                id =>
                  db
                    .updateBook(id, BookData.partialFromHttpQuery(requestQry))
                    .fold(
                      serverFailure,
                      _ => seeOther("/books")
                    )
              )
          )

      def delete(bookId: String): UIO[Response] =
        toBookId(bookId)
          .foldM(
            _ => ZIO.succeed(badRequest(s"Cannot parse ID $bookId")),
            id =>
              db
                .deleteBook(id)
                .fold(
                  serverFailure,
                  _ => seeOther("/books")
                )
          )
    }

  private val effect: URIO[Has[Database], BookHandler] =
    ZIO.service[Database].map(fromDatabase)

  val layer: URLayer[Has[Database], Has[BookHandler]] = effect.toLayer
}
