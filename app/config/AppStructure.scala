package config

import controllers.{App, AssetsComponents}
import play.api.ApplicationLoader.Context
import play.api.BuiltInComponentsFromContext
import play.api.db.slick.{DbName, SlickComponents}
import play.filters.HttpFiltersComponents
import router.Routes
import slick.jdbc.H2Profile

class AppStructure(context: Context)
    extends BuiltInComponentsFromContext(context)
    with SlickComponents
    with AssetsComponents
    with HttpFiltersComponents {
  lazy val app    = new App(controllerComponents)(slickApi.dbConfig[H2Profile](DbName("default")))
  lazy val router = new Routes(httpErrorHandler, app)
}
