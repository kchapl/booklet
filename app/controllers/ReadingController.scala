package controllers

import model.{Book, Reading}
import play.api.db.Database
import play.api.mvc._
import zio.{ZEnv, ZIO}

class ReadingController(components: ControllerComponents)(implicit db: Database)
    extends AbstractController(components) {

  private val runtime = zio.Runtime.default

  def listReadings(): Action[AnyContent] = Action { _ =>
    val books    = Book.all
    val readings = Reading.all
    readings.foreach(println)
    println(readings.size)
    Ok(views.html.readings(books))
  }

  def listReadings2(): Action[AnyContent] = zioAction { _ =>
    val books = Seq(
      Book(id = "1", author = "a1", title = "t1"),
      Book(id = "2", author = "a1", title = "t2"),
      Book(id = "3", author = "a2", title = "t4")
    )
    ZIO(Ok(views.html.readings(books)))
  }

  private def zioAction(
      result: Request[AnyContent] => ZIO[ZEnv, Throwable, Result]
  ): Action[AnyContent] =
    Action(request => runtime.unsafeRun(result(request)))
}
