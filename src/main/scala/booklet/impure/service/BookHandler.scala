package booklet.impure.service

import booklet.pure.Failure
import booklet.pure.http.CustomResponse._
import booklet.pure.http.Query
import booklet.pure.model.{BookData, BookId}
import booklet.pure.views.BookView
import zhttp.http.Path.Segment
import zhttp.http.{Body, Path, Request, Response}
import zio.{IO, UIO, URLayer, ZIO, ZLayer}

trait BookHandler {
  def fetchAll(userId: String): IO[Failure, Response]

  def fetch(bookId: String, userId: String): IO[Failure, Response]

  def create(request: Request, userId: String): IO[Failure, Response]

  def update(bookId: String, request: Request, userId: String): IO[Failure, Response]

  def delete(bookId: String, userId: String): IO[Failure, Response]
}

object BookHandler {
  def fetchAll(userId: String): ZIO[BookHandler, Failure, Response] =
    ZIO.serviceWithZIO(_.fetchAll(userId))

  def fetch(bookId: String, userId: String): ZIO[BookHandler, Failure, Response] =
    ZIO.serviceWithZIO(_.fetch(bookId, userId))

  def create(request: Request, userId: String): ZIO[BookHandler, Failure, Response] =
    ZIO.serviceWithZIO(_.create(request, userId))

  def update(
      bookId: String,
      request: Request,
      userId: String
  ): ZIO[BookHandler, Failure, Response] =
    ZIO.serviceWithZIO(_.update(bookId, request, userId))

  def delete(bookId: String, userId: String): ZIO[BookHandler, Failure, Response] =
    ZIO.serviceWithZIO(_.delete(bookId, userId))
}

object BookHandlerLive {

  private val booksPath = "/books"

  private def toBookId(bookId: String) =
    ZIO
      .fromOption(bookId.toLongOption)
      .map(BookId(_))

  private def fetchAllImpl(db: GoogleSheetsService, userId: String) =
    db.fetchAllBooks(userId)
      .fold(
        serverFailure,
        books => ok(Body.fromString(BookView.list(books)))
      )

  private def fetchImpl(db: GoogleSheetsService, bookId: String, userId: String) =
    toBookId(bookId)
      .foldZIO(
        _ => ZIO.succeed(badRequest(s"Cannot parse ID $bookId")),
        id =>
          db
            .fetchBook(id, userId)
            .fold(
              serverFailure,
              {
                case None       => notFound(Path(Vector(Segment(bookId))))
                case Some(book) => ok(Body.fromString(BookView.list(Seq(book))))
              }
            )
      )

  private def createImpl(db: GoogleSheetsService, request: Request, userId: String) =
    Query
      .fromRequest(request)
      .flatMap(requestQry =>
        ZIO
          .fromOption(BookData.completeFromHttpQuery(requestQry))
          .foldZIO(
            _ => ZIO.succeed(badRequest(requestQry.toString)),
            bookData =>
              db
                .insertBook(bookData.copy(userId = Some(userId)), userId)
                .fold(
                  serverFailure,
                  _ => seeOther(booksPath)
                )
          )
      )

  private def updateImpl(
      db: GoogleSheetsService,
      bookId: String,
      request: Request,
      userId: String
  ) =
    Query
      .fromRequest(request)
      .flatMap(requestQry =>
        toBookId(bookId)
          .foldZIO(
            _ => ZIO.succeed(badRequest(requestQry.toString)),
            id =>
              db
                .updateBook(id, BookData.partialFromHttpQuery(requestQry), userId)
                .fold(
                  serverFailure,
                  _ => seeOther(booksPath)
                )
          )
      )

  private def deleteImpl(db: GoogleSheetsService, bookId: String, userId: String) =
    toBookId(bookId)
      .foldZIO(
        _ => ZIO.succeed(badRequest(s"Cannot parse ID $bookId")),
        id =>
          db
            .deleteBook(id, userId)
            .fold(
              serverFailure,
              _ => seeOther(booksPath)
            )
      )

  private def fromDatabase(db: GoogleSheetsService) =
    new BookHandler {
      override def fetchAll(userId: String): UIO[Response] = fetchAllImpl(db, userId)

      override def fetch(bookId: String, userId: String): UIO[Response] =
        fetchImpl(db, bookId, userId)

      override def create(request: Request, userId: String): IO[Failure, Response] =
        createImpl(db, request, userId)

      override def update(bookId: String, request: Request, userId: String): IO[Failure, Response] =
        updateImpl(db, bookId, request, userId)

      override def delete(bookId: String, userId: String): UIO[Response] =
        deleteImpl(db, bookId, userId)
    }

  val layer: URLayer[GoogleSheetsService, BookHandler] =
    ZLayer.fromZIO(ZIO.service[GoogleSheetsService].map(fromDatabase))
}
