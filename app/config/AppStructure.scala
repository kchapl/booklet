package config

import controllers.{App, ReadingController}
import play.api.ApplicationLoader.Context
import play.api.BuiltInComponentsFromContext
import play.filters.HttpFiltersComponents
import router.Routes

class AppStructure(context: Context)
    extends BuiltInComponentsFromContext(context)
    with HttpFiltersComponents {
  lazy val app =
    new App(
      controllerComponents
    )
  lazy val readingController =
    new ReadingController(controllerComponents)
  lazy val router =
    new Routes(httpErrorHandler, app, readingController)
}
