package booklet.services

import zio._

import scala.io.Source

trait StaticFile {
  def fetchContent(path: String): Task[String]
}

object StaticFile {
  def fetchContent(path: String): RIO[StaticFile, String] =
    RIO.serviceWithZIO(_.fetchContent(path))
}

object StaticFileLive {
  private val effect: Task[StaticFile] =
    Task.attempt(path =>
      Task.acquireReleaseWith(Task.attempt(Source.fromResource(path)))(src =>
        Task.succeed(src.close())
      )(src => Task.succeed(src.getLines().toList.mkString("\n")))
    )
  val layer: TaskLayer[StaticFile] = effect.toLayer
}
