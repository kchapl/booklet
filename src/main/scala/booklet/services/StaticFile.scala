package booklet.services

import zio.{Has, RIO, Task, TaskLayer}

import scala.io.Source

trait StaticFile {
  def fetchContent(path: String): Task[String]
}

object StaticFile {
  def fetchContent(path: String): RIO[Has[StaticFile], String] =
    RIO.serviceWith(_.fetchContent(path))
}

object StaticFileLive {
  private val effect: Task[StaticFile] =
    Task.effect(path =>
      Task.bracket(Task.effect(Source.fromResource(path)))(x => Task.succeed(x.close()))(x =>
        Task.succeed(x.getLines().toList.mkString("\n"))
      )
    )
  val layer: TaskLayer[Has[StaticFile]] = effect.toLayer
}
