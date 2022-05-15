package booklet.services.book_handler

import booklet.http.CustomResponse
import booklet.model._
import booklet.services.database.Database
import booklet.views.BookView
import zio._
import zio.mock.Expectation._
import zio.mock._
import zio.test.Assertion._
import zio.test._

object BookHandlerLiveSpec extends ZIOSpecDefault {

  object MockDatabase extends Mock[Database] {

    object FetchAllBooks extends Effect[Unit, booklet.Failure, List[Book]]

    def fail[A](): IO[booklet.Failure, A] =
      ZIO.fail(booklet.Failure(message = "Unexpected", cause = None))

    val compose: URLayer[Proxy, Database] =
      ZLayer.fromZIO(
        ZIO
          .service[Proxy]
          .map { proxy =>
            new Database {
              val fetchAllBooks: IO[booklet.Failure, List[Book]] = proxy(FetchAllBooks)
              val fetchAllReadings: IO[booklet.Failure, List[Reading]] = fail()

              def fetchBook(id: BookId): IO[booklet.Failure, Option[Book]] = fail()

              def fetchReading(id: ReadingId): IO[booklet.Failure, Option[Reading]] = fail()

              def insertBook(data: BookData): IO[booklet.Failure, Unit] = fail()

              def insertReading(data: ReadingData): IO[booklet.Failure, Unit] = fail()

              def updateBook(id: BookId, data: BookData): IO[booklet.Failure, Unit] = fail()

              def updateReading(id: ReadingId, data: ReadingData): IO[booklet.Failure, Unit] =
                fail()

              def deleteBook(id: BookId): IO[booklet.Failure, Unit] = fail()

              def deleteReading(id: ReadingId): IO[booklet.Failure, Unit] = fail()
            }
          }
      )
  }

  // noinspection TypeAnnotation
  val spec =
    suite("BookHandlerLiveSpec")(
      suite("fetchAll")(
        test("succeeds with a list of books") {

          val books = List(
            Book(
              id = BookId(1),
              isbn = Isbn("i"),
              author = Author("a"),
              title = Title("t"),
              subtitle = None,
              thumbnail = None,
              smallThumbnail = None
            )
          )
          val mockEnv = MockDatabase.FetchAllBooks(value(books))

          val app = BookHandler.fetchAll
          val out = app.provide(mockEnv, BookHandlerLive.layer)
          assertZIO(out)(equalTo(CustomResponse.ok(BookView.list(books))))
        },
        test("succeeds with an empty list of books") {

          val books = Nil
          val mockEnv = MockDatabase.FetchAllBooks(value(books))

          val app = BookHandler.fetchAll
          val out = app.provide(mockEnv.toLayer, BookHandlerLive.layer)
          assertZIO(out)(equalTo(CustomResponse.ok(BookView.list(books))))
        }
      )
    )
}
