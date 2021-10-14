package booklet.model

import upickle.default._

import java.time.LocalDate
import scala.util.Try

case class ReadingData(bookId: BookId, completed: LocalDate, rating: Rating)

object ReadingData {
  implicit val writer: Writer[ReadingData] = macroW
  implicit val dateWriter: ReadWriter[LocalDate] =
    readwriter[String].bimap[LocalDate](_.toString, LocalDate.parse)

  def completeFromHttpQuery(qry: Map[String, String]): Option[ReadingData] = for {
    bookIdStr <- qry.get("bookId")
    bookId <- bookIdStr.toLongOption
    completedStr <- qry.get("completed")
    completed <- Try(LocalDate.parse(completedStr)).toOption
    ratingStr <- qry.get("rating")
    rating <- ratingStr.toIntOption
  } yield ReadingData(
    bookId = BookId(bookId),
    completed = completed,
    rating = Rating(rating)
  )
}
