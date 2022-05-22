package booklet.services.book_handler

import booklet.Failure
import booklet.http.CustomResponse._
import booklet.http.Query
import booklet.model.{BookData, BookId}
import booklet.services.database.Database
import booklet.views.BookView
import zhttp.http._
import zio._

object BookHandlerLive {

  private val booksPath = "/books"

  private def toBookId(bookId: String) =
    ZIO
      .fromOption(bookId.toLongOption)
      .map(BookId(_))

  private def fetchAllImpl(db: Database) =
    db.fetchAllBooks
      .fold(
        serverFailure,
        books => ok(BookView.list(books))
      )

  private def fetchImpl(db: Database, bookId: String) =
    toBookId(bookId)
      .foldZIO(
        _ => ZIO.succeed(badRequest(s"Cannot parse ID $bookId")),
        id =>
          db
            .fetchBook(id)
            .fold(
              serverFailure,
              {
                case None       => notFound(Path(bookId))
                case Some(book) => ok(BookView.list(Seq(book)))
              }
            )
      )

  private def createImpl(db: Database, request: Request) =
    Query
      .fromRequest(request)
      .flatMap(requestQry =>
        ZIO
          .fromOption(BookData.completeFromHttpQuery(requestQry))
          .foldZIO(
            _ => ZIO.succeed(badRequest(requestQry.toString)),
            bookData =>
              db
                .insertBook(bookData)
                .fold(
                  serverFailure,
                  _ => seeOther(booksPath)
                )
          )
      )

  private def updateImpl(db: Database, bookId: String)(request: Request) =
    Query
      .fromRequest(request)
      .flatMap(requestQry =>
        toBookId(bookId)
          .foldZIO(
            _ => ZIO.succeed(badRequest(requestQry.toString)),
            id =>
              db
                .updateBook(id, BookData.partialFromHttpQuery(requestQry))
                .fold(
                  serverFailure,
                  _ => seeOther(booksPath)
                )
          )
      )

  private def deleteImpl(db: Database, bookId: String) =
    toBookId(bookId)
      .foldZIO(
        _ => ZIO.succeed(badRequest(s"Cannot parse ID $bookId")),
        id =>
          db
            .deleteBook(id)
            .fold(
              serverFailure,
              _ => seeOther(booksPath)
            )
      )

  private def fromDatabase(db: Database) =
    new BookHandler {
      override val fetchAll: UIO[Response] = fetchAllImpl(db)

      override def fetch(bookId: String): UIO[Response] = fetchImpl(db, bookId)

      override def create(request: Request): IO[Failure, Response] = createImpl(db, request)

      override def update(bookId: String)(request: Request): IO[Failure, Response] =
        updateImpl(db, bookId)(request)

      override def delete(bookId: String): UIO[Response] = deleteImpl(db, bookId)
    }

  val layer: URLayer[Database, BookHandler] =
    ZLayer.fromZIO(ZIO.service[Database].map(fromDatabase))
}
