package booklet.impure

import booklet.impure.router._
import booklet.impure.service._
import booklet.impure.service.bookfinder.{GoogleBookFinder, GoogleBookFinderLive}
import booklet.impure.service.database.DatabaseLive
import zhttp.http._
import zhttp.service.{ChannelFactory, EventLoopGroup, Server}
import zio._

object Main extends ZIOAppDefault {

  private val program =
    for {
      config <- Config.service
      apps: Http[
        GoogleBookFinder with ReadingHandler with BookHandler with StaticFile,
        Nothing,
        Request,
        Response
      ] =
        RootRouter.app ++ StaticRouter.app ++ BookRouter.app ++ ReadingRouter.app ++ BookFinderRouter.app
      _ <- Server.start(
        port = config.app.port,
        http = apps
      )
    } yield ()

  override def run: ZIO[ZIOAppArgs, Any, Any] =
    program
      .provide(
        ConfigLive.layer,
        DatabaseLive.layer,
        BookHandlerLive.layer,
        ReadingHandlerLive.layer,
        StaticFileLive.layer,
        GoogleBookFinderLive.layer,
        EventLoopGroup.auto(),
        ChannelFactory.auto
      )
      .forever
}
