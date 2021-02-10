package services

import model.Reading
import zio._

package object database {

  type Database = Has[Database.Service]

  object Database {

    trait Service {
      def fetchAllReadings(): Task[List[Reading]]
      def insertReading(reading: Reading): Task[Unit]
    }

    def fetchAllReadings(): RIO[Database, List[Reading]] =
      RIO.accessM(_.get.fetchAllReadings())

    def insertReading(reading: Reading): RIO[Database, Unit] =
      RIO.accessM(_.get.insertReading(reading))
  }
}
