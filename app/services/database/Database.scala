package services.database

import model.Reading
import zio._

import java.time.LocalDate

object Database {

  trait Service {
    def fetchAllReadings(): Task[List[Reading]]
    def insertReading(
        author: String,
        title: String,
        thumbnail: Option[String],
        smallThumbnail: Option[String],
        completed: LocalDate,
        rating: Int
    ): Task[Unit]
    def deleteReading(reading: Reading): Task[Unit]
  }

  def fetchAllReadings(): RIO[Database, List[Reading]] =
    RIO.accessM(_.get.fetchAllReadings())

  def insertReading(
      author: String,
      title: String,
      thumbnail: Option[String],
      smallThumbnail: Option[String],
      completed: LocalDate,
      rating: Int
  ): RIO[Database, Unit] =
    RIO.accessM(
      _.get.insertReading(
        author: String,
        title: String,
        thumbnail: Option[String],
        smallThumbnail: Option[String],
        completed: LocalDate,
        rating: Int
      )
    )

  def deleteReading(reading: Reading): RIO[Database, Unit] =
    RIO.accessM(_.get.deleteReading(reading))
}
