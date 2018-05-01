package config

import controllers.App
import play.api.ApplicationLoader.Context
import play.api.BuiltInComponentsFromContext
import play.api.db.evolutions.EvolutionsComponents
import play.api.db.{DBComponents, HikariCPComponents}
import play.filters.HttpFiltersComponents
import router.Routes

class AppStructure(context: Context)
    extends BuiltInComponentsFromContext(context)
    with DBComponents
    with EvolutionsComponents
    with HikariCPComponents
    // with SlickComponents
    // with AssetsComponents
    with HttpFiltersComponents {
  lazy val app    = new App(controllerComponents) //(slickApi.dbConfig[H2Profile](DbName("default")))
  lazy val router = new Routes(httpErrorHandler, app)

  applicationEvolutions
}
