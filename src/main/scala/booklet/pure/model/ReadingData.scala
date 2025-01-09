package booklet.pure.model

import zio.json.{DeriveJsonEncoder, JsonEncoder}

import java.time.LocalDate
import scala.util.Try

case class ReadingData(bookId: Option[BookId], completed: Option[LocalDate], rating: Option[Rating], userId: Option[String])

object ReadingData {
  implicit val encoder: JsonEncoder[ReadingData] = DeriveJsonEncoder.gen[ReadingData]

  def completeFromHttpQuery(qry: Map[String, String]): Option[ReadingData] = for {
    bookIdStr <- qry.get("bookId")
    bookId <- bookIdStr.toLongOption
    completedStr <- qry.get("completed")
    completed <- Try(LocalDate.parse(completedStr)).toOption
    ratingStr <- qry.get("rating")
    rating <- ratingStr.toIntOption
    userId <- qry.get("userId")
  } yield ReadingData(
    bookId = Some(BookId(bookId)),
    completed = Some(completed),
    rating = Some(Rating(rating)),
    userId = Some(userId)
  )

  def partialFromHttpQuery(qry: Map[String, String]): ReadingData = ReadingData(
    bookId = None,
    completed = qry.get("completed").map(LocalDate.parse),
    rating = qry.get("rating").flatMap(_.toIntOption.map(Rating.apply)),
    userId = qry.get("userId")
  )
}
