package booklet.services.book_handler

import booklet.http.CustomResponse._
import booklet.http.Query
import booklet.model.{BookData, BookId}
import booklet.services.database.Database
import booklet.views.BookView
import zhttp.http.{Request, UResponse}
import zio.{Has, UIO, URLayer, ZIO}

object BookHandlerLive {

  val layer: URLayer[Has[Database], Has[BookHandler]] =
    ZIO.service[Database].map(toBookHandler).toLayer

  private def toBookHandler(db: Database): BookHandler =
    new BookHandler {
      val fetchAll: UIO[UResponse] = fetchAllFrom(db)

      def fetch(bookId: String): UIO[UResponse] = fetchFrom(db)(bookId)

      def create(request: Request): UIO[UResponse] = createFrom(db)(request)

      def update(bookId: String)(request: Request): UIO[UResponse] = updateFrom(db)(bookId)(request)

      def delete(bookId: String): UIO[UResponse] = deleteFrom(db)(bookId)
    }

  private def toBookId(bookId: String): ZIO[Any, Option[Nothing], BookId] =
    ZIO
      .fromOption(bookId.toLongOption)
      .map(BookId(_))

  private def fetchAllFrom(db: Database) =
    db.fetchAllBooks
      .fold(
        serverFailure,
        books => ok(BookView.list(books).toString)
      )

  private def fetchFrom(db: Database)(bookId: String) =
    toBookId(bookId)
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

  private def createFrom(db: Database)(request: Request) = {
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

  private def updateFrom(db: Database)(bookId: String)(request: Request) = {
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

  private def deleteFrom(db: Database)(bookId: String) =
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
