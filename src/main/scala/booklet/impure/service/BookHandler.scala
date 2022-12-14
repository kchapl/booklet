package booklet.impure.service

import booklet.impure.service.database.Database
import booklet.pure.Failure
import booklet.pure.http.CustomResponse._
import booklet.pure.http.Query
import booklet.pure.model.{BookData, BookId}
import booklet.pure.views.BookView
import zhttp.http.Path.Segment
import zhttp.http.{Body, Path, Request, Response}
import zio.{IO, UIO, URLayer, ZIO, ZLayer}

trait BookHandler {
  def fetchAll: IO[Failure, Response]

  def fetch(bookId: String): IO[Failure, Response]

  def create(request: Request): IO[Failure, Response]

  def update(bookId: String)(request: Request): IO[Failure, Response]

  def delete(bookId: String): IO[Failure, Response]
}

object BookHandler {
  val fetchAll: ZIO[BookHandler, Failure, Response] = ZIO.serviceWithZIO(_.fetchAll)

  def fetch(bookId: String): ZIO[BookHandler, Failure, Response] =
    ZIO.serviceWithZIO(_.fetch(bookId))

  def create(request: Request): ZIO[BookHandler, Failure, Response] =
    ZIO.serviceWithZIO(_.create(request))

  def update(bookId: String)(request: Request): ZIO[BookHandler, Failure, Response] =
    ZIO.serviceWithZIO(_.update(bookId)(request))

  def delete(bookId: String): ZIO[BookHandler, Failure, Response] =
    ZIO.serviceWithZIO(_.delete(bookId))
}

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
        books => ok(Body.fromString(BookView.list(books)))
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
                case None       => notFound(Path(Vector(Segment(bookId))))
                case Some(book) => ok(Body.fromString(BookView.list(Seq(book))))
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
