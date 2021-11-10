package booklet.services.book_handler

import booklet.http.CustomResponse._
import booklet.http.Query
import booklet.model.{BookData, BookId}
import booklet.services.database.Database
import booklet.views.BookView
import zhttp.http._
import zio.{Has, UIO, URIO, URLayer, ZIO}

object BookHandlerLive {

  private def toBookId(bookId: String) =
    ZIO
      .fromOption(bookId.toLongOption)
      .map(BookId(_))

  private def fromDatabase(db: Database) =
    new BookHandler {

      def fetchAll: UIO[UResponse] =
        db.fetchAllBooks
          .fold(
            serverFailure,
            books => ok(BookView.list(books).toString)
          )

      def fetch(bookId: String): UIO[UResponse] =
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

      def create(request: Request): UIO[UResponse] = {
        val requestQry = Query.fromRequest(request)
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
      }

      def update(bookId: String)(request: Request): UIO[UResponse] = {
        val requestQry = Query.fromRequest(request)
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
      }

      def delete(bookId: String): UIO[UResponse] =
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
