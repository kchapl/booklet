package booklet.services.database

import booklet.model._
import booklet.{Config, Failure}
import doobie.implicits._
import doobie.util.Get
import doobie.util.fragment.Fragment
import doobie.util.fragments.setOpt
import doobie.{Put, Read, Transactor}
import zio._
import zio.interop.catz._
import zio.interop.catz.implicits.rts

import java.sql
import java.time.LocalDate
import java.util.Date

object DatabaseLive {

  private object Query {

    val fetchAllBooks: Fragment =
      fr"SELECT id, isbn, author, title FROM books"

    private val readingSelectClause =
      fr"SELECT r.id, b.id, b.isbn, b.author, b.title, r.completed, r.rating"

    val fetchAllReadings: Fragment =
      readingSelectClause ++
        fr"FROM books b JOIN readings r" ++
        fr"ON r.book_id = b.id"

    def fetchBook(id: BookId): Fragment =
      fr"SELECT id, isbn, author, title FROM books WHERE id = ${id.value}"

    def fetchReading(id: ReadingId): Fragment =
      readingSelectClause ++
        fr"FROM books" ++
        fr"JOIN readings r" ++
        fr"ON r.book_id = b.id" ++
        fr"WHERE r.id = $id"

    def insertBook(data: BookData): Fragment =
      fr"INSERT INTO books(isbn, author, title)" ++
        fr"VALUES(${data.isbn}, ${data.author}, ${data.title})"

    def insertReading(data: ReadingData): Fragment =
      fr"INSERT INTO readings(book_id, completed, rating)" ++
        fr"VALUES(${data.bookId}, ${data.completed}, ${data.rating})"

    def updateBook(id: BookId, data: BookData): Fragment =
      fr"UPDATE books" ++ setOpt(
        data.isbn.map(isbn => fr"isbn = ${isbn.value}"),
        data.author.map(author => fr"author = ${author.value}"),
        data.title.map(title => fr"title = ${title.value}")
      ) ++ fr"WHERE id=$id"

    def updateReading(id: ReadingId, data: ReadingData): Fragment =
      fr"UPDATE readings" ++ setOpt(
        data.completed.map(completed => fr"completed = ${completed.toString}"),
        data.rating.map(rating => fr"rating = ${rating.value}")
      ) ++ fr"WHERE id=$id"

    def deleteBook(id: BookId): Fragment =
      fr"DELETE" ++
        fr"FROM books" ++
        fr"WHERE id = $id"

    def deleteReading(id: ReadingId): Fragment =
      fr"DELETE" ++
        fr"FROM readings" ++
        fr"WHERE id = $id"
  }

  implicit val dateGet: Get[LocalDate] =
    Get[Date].map(date => new sql.Date(date.getTime).toLocalDate)

  implicit val datePut: Put[LocalDate] =
    Put[Date].contramap(date => sql.Date.valueOf(date))

  implicit val bookRead: Read[Book] =
    Read[(Long, String, String, String)].map { case (id, isbn, author, title) =>
      Book(BookId(id), Isbn(isbn), Author(author), Title(title), None, None, None)
    }

  implicit val readingRead: Read[Reading] =
    Read[(Long, Book, LocalDate, Int)].map { case (id, book, completed, rating) =>
      Reading(ReadingId(id), book, completed, Rating(rating))
    }

  private def fetchAllBooksImpl(xa: Transactor[Task]) =
    Query.fetchAllBooks
      .query[Book]
      .to[List]
      .transact(xa)
      .mapError(Failure.fromThrowable)

  private def fetchAllReadingsImpl(xa: Transactor[Task]) =
    Query.fetchAllReadings
      .query[Reading]
      .to[List]
      .transact(xa)
      .mapError(Failure.fromThrowable)

  private def fetchBookImpl(xa: Transactor[Task], id: BookId) =
    Query
      .fetchBook(id)
      .query[Book]
      .option
      .transact(xa)
      .mapError(Failure.fromThrowable)

  private def fetchReadingImpl(xa: Transactor[Task], id: ReadingId) =
    Query
      .fetchReading(id)
      .query[Reading]
      .option
      .transact(xa)
      .mapError(Failure.fromThrowable)

  private def insertBookImpl(xa: Transactor[Task], data: BookData) =
    Query
      .insertBook(data)
      .update
      .withUniqueGeneratedKeys[Long]("id")
      .transact(xa)
      .mapBoth(
        Failure.fromThrowable,
        _ => ()
      )

  private def insertReadingImpl(xa: Transactor[Task], data: ReadingData) =
    Query
      .insertReading(data)
      .update
      .withUniqueGeneratedKeys[Long]("id")
      .transact(xa)
      .mapBoth(
        Failure.fromThrowable,
        _ => ()
      )

  private def updateBookImpl(xa: Transactor[Task], id: BookId, data: BookData) =
    Query
      .updateBook(id, data)
      .update
      .run
      .transact(xa)
      .mapBoth(
        Failure.fromThrowable,
        _ => ()
      )

  private def updateReadingImpl(xa: Transactor[Task], id: ReadingId, data: ReadingData) =
    Query
      .updateReading(id, data)
      .update
      .run
      .transact(xa)
      .mapBoth(
        Failure.fromThrowable,
        _ => ()
      )

  private def deleteBookImpl(xa: Transactor[Task], id: BookId) =
    Query
      .deleteBook(id)
      .update
      .run
      .transact(xa)
      .mapBoth(
        Failure.fromThrowable,
        _ => ()
      )

  private def deleteReadingImpl(xa: Transactor[Task], id: ReadingId) =
    Query
      .deleteReading(id)
      .update
      .run
      .transact(xa)
      .mapBoth(
        Failure.fromThrowable,
        _ => ()
      )

  private def toDatabase(xa: Transactor[Task]): Database =
    new Database {

      val fetchAllBooks: ZIO[Any, Failure, List[Book]] = fetchAllBooksImpl(xa)

      val fetchAllReadings: ZIO[Any, Failure, List[Reading]] = fetchAllReadingsImpl(xa)

      def fetchBook(id: BookId): ZIO[Any, Failure, Option[Book]] = fetchBookImpl(xa, id)

      def fetchReading(id: ReadingId): ZIO[Any, Failure, Option[Reading]] = fetchReadingImpl(xa, id)

      def insertBook(data: BookData): ZIO[Any, Failure, Unit] = insertBookImpl(xa, data)

      def insertReading(data: ReadingData): ZIO[Any, Failure, Unit] = insertReadingImpl(xa, data)

      def updateBook(id: BookId, data: BookData): ZIO[Any, Failure, Unit] =
        updateBookImpl(xa, id, data)

      def updateReading(id: ReadingId, data: ReadingData): ZIO[Any, Failure, Unit] =
        updateReadingImpl(xa, id, data)

      def deleteBook(id: BookId): ZIO[Any, Failure, Unit] = deleteBookImpl(xa, id)

      def deleteReading(id: ReadingId): ZIO[Any, Failure, Unit] = deleteReadingImpl(xa, id)
    }

  val layer: ZLayer[Config, Failure, Database] = ZLayer.fromZIO(for {
    config <- ZIO.service[Config]
  } yield {
    val xa = {
      val jdbc = JdbcConfig.fromDbUrl(config.db.url)
      Transactor
        .fromDriverManager[Task](
          driver = config.db.driver,
          url = jdbc.url,
          user = jdbc.userName.getOrElse(""),
          pass = jdbc.password.getOrElse("")
        )
    }
    toDatabase(xa)
  })
}
