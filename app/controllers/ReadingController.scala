package controllers

import model.{Book, Reading}
import play.api.db.Database
import play.api.mvc._

class ReadingController(components: ControllerComponents)(implicit db: Database)
    extends AbstractController(components) {

  def listReadings() = Action { _ =>
    val books    = Book.all
    val readings = Reading.all
    readings.foreach(println)
    println(readings.size)
    Ok(views.html.readings(books))
  }
}
