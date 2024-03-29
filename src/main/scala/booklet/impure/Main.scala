package booklet.impure

import booklet.impure.router._
import booklet.impure.service._
import booklet.impure.service.bookfinder.{GoogleBookFinder, GoogleBookFinderLive}
import booklet.impure.service.database.DoobieDatabase
import zhttp.http._
import zhttp.service.{ChannelFactory, EventLoopGroup, Server}
import zio.{ZIO, ZIOAppArgs, ZIOAppDefault}

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
        RootRouter.app ++ BookRouter.app ++ ReadingRouter.app ++ BookFinderRouter.app
      _ <- Server.start(
        port = config.app.port,
        http = apps
      )
    } yield ()

  override def run: ZIO[ZIOAppArgs, Any, Any] =
    program
      .provide(
        ConfigLive.layer,
        DoobieDatabase.layer,
        BookHandlerLive.layer,
        ReadingHandlerLive.layer,
        StaticFileLive.layer,
        GoogleBookFinderLive.layer,
        EventLoopGroup.auto(),
        ChannelFactory.auto
      )
      .forever
}
