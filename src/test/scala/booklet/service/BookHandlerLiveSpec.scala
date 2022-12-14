package booklet.service

import booklet.impure.service.database.Database
import booklet.impure.service.{BookHandler, BookHandlerLive}
import booklet.pure.Failure
import booklet.pure.model._
import booklet.pure.views.BookView
import zio._
import zio.mock.Expectation._
import zio.mock._
import zio.test.Assertion._
import zio.test._

object BookHandlerLiveSpec extends ZIOSpecDefault {

  object MockDatabase extends Mock[Database] {

    object FetchAllBooks extends Effect[Unit, Failure, List[Book]]

    def fail[A](): IO[Failure, A] =
      ZIO.fail(Failure(message = "Unexpected", cause = None))

    val compose: URLayer[Proxy, Database] =
      ZLayer.fromZIO(
        ZIO
          .service[Proxy]
          .map { proxy =>
            new Database {
              val fetchAllBooks: IO[Failure, List[Book]] = proxy(FetchAllBooks)

              val fetchAllReadings: IO[Failure, List[Reading]] = fail()

              def fetchBook(id: BookId): IO[Failure, Option[Book]] = fail()

              def fetchReading(id: ReadingId): IO[Failure, Option[Reading]] = fail()

              def insertBook(data: BookData): IO[Failure, Unit] = fail()

              def insertReading(data: ReadingData): IO[Failure, Unit] = fail()

              def updateBook(id: BookId, data: BookData): IO[Failure, Unit] = fail()

              def updateReading(id: ReadingId, data: ReadingData): IO[Failure, Unit] =
                fail()

              def deleteBook(id: BookId): IO[Failure, Unit] = fail()

              def deleteReading(id: ReadingId): IO[Failure, Unit] = fail()
            }
          }
      )
  }

  val spec: Spec[TestEnvironment, Any] =
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

          assertZIO(out.flatMap(_.body.asString))(equalTo(BookView.list(books)))
        },
        test("succeeds with an empty list of books") {

          val books = Nil
          val mockEnv = MockDatabase.FetchAllBooks(value(books))

          val app = BookHandler.fetchAll
          val out = app.provide(mockEnv, BookHandlerLive.layer)
          assertZIO(out.flatMap(_.body.asString))(equalTo(BookView.list(books)))
        }
      )
    )
}
