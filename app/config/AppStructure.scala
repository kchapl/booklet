package config

import controllers.{App, AuthController, BookController, ReadingController}
import play.api.ApplicationLoader.Context
import play.api.BuiltInComponentsFromContext
import play.filters.HttpFiltersComponents
import router.Routes

class AppStructure(context: Context)
    extends BuiltInComponentsFromContext(context)
    with HttpFiltersComponents
    with controllers.AssetsComponents {

  lazy val app = new App(controllerComponents)

  lazy val authController    = new AuthController(controllerComponents)
  lazy val bookController    = new BookController(controllerComponents)
  lazy val readingController = new ReadingController(controllerComponents)

  lazy val router =
    new Routes(httpErrorHandler, app, authController, bookController, readingController, assets)
}
