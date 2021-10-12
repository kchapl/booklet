package booklet.services.reading_handler

import zhttp.http.UResponse
import zio.{Has, RIO, UIO}

trait ReadingHandler {
  def fetchAll: UIO[UResponse]
  def fetch(readingId: String): UIO[UResponse]
}

object ReadingHandler {
  val fetchAll: RIO[Has[ReadingHandler], UResponse] = RIO.serviceWith(_.fetchAll)
  def fetch(readingId: String): RIO[Has[ReadingHandler], UResponse] =
    RIO.serviceWith(_.fetch(readingId))
}
