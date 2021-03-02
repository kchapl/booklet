package services.database

import model.{Reading, ReadingToInsert}
import zio._

object Database {

  trait Service {
    def fetchAllReadings(): Task[List[Reading]]
    def insertReading(readingToInsert: ReadingToInsert): Task[Unit]
    def deleteReading(reading: Reading): Task[Unit]
  }

  def fetchAllReadings(): RIO[Database, List[Reading]] =
    RIO.accessM(_.get.fetchAllReadings())

  def insertReading(readingToInsert: ReadingToInsert): RIO[Database, Unit] =
    RIO.accessM(_.get.insertReading(readingToInsert))

  def deleteReading(reading: Reading): RIO[Database, Unit] =
    RIO.accessM(_.get.deleteReading(reading))
}
