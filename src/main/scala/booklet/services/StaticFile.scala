package booklet.services

import zio._

import scala.io.Source

trait StaticFile {
  def fetchContent(path: String): Task[String]
}

object StaticFile {
  def fetchContent(path: String): RIO[StaticFile, String] =
    ZIO.serviceWithZIO(_.fetchContent(path))
}

object StaticFileLive {
  val layer: ULayer[StaticFile] = ZLayer.succeed(new StaticFile {
    override def fetchContent(path: String): Task[String] =
      ZIO
        .scoped(ZIO.fromAutoCloseable(ZIO.attempt(Source.fromResource(path))))
        .map(_.getLines().toList.mkString("\n"))
  })
}
