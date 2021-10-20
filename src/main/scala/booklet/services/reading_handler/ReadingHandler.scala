package booklet.services.reading_handler

import zhttp.http.{Request, UResponse}
import zio.{Has, RIO, UIO}

trait ReadingHandler {
  def fetchAll: UIO[UResponse]
  def fetch(readingId: String): UIO[UResponse]
  def create(request: Request): UIO[UResponse]
  def update(readingId: String)(request: Request): UIO[UResponse]
  def delete(readingId: String): UIO[UResponse]
}

object ReadingHandler {
  val fetchAll: RIO[Has[ReadingHandler], UResponse] = RIO.serviceWith(_.fetchAll)
  def fetch(readingId: String): RIO[Has[ReadingHandler], UResponse] =
    RIO.serviceWith(_.fetch(readingId))
  def create(request: Request): RIO[Has[ReadingHandler], UResponse] =
    RIO.serviceWith(_.create(request))
  def update(readingId: String)(request: Request): RIO[Has[ReadingHandler], UResponse] =
    RIO.serviceWith(_.update(readingId)(request))
  def delete(readingId: String): RIO[Has[ReadingHandler], UResponse] =
    RIO.serviceWith(_.delete(readingId))
}
