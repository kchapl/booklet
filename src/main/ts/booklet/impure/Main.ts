import { Config } from './Config';
import { ConfigLive } from './ConfigLive';
import { BookRouter } from './router/BookRouter';
import { ReadingRouter } from './router/ReadingRouter';
import { RootRouter } from './router/RootRouter';
import { GoogleBookFinder, GoogleBookFinderLive } from './service/GoogleBookFinder';
import { GoogleSheetsServiceLive } from './service/GoogleSheetsService';
import { BookHandlerLive } from './service/BookHandler';
import { ReadingHandlerLive } from './service/ReadingHandler';
import { StaticFileLive } from './service/StaticFile';
import { Http, Request, Response } from 'zhttp/http';
import { Server } from 'zhttp/service';
import { ZIO, ZIOAppArgs, ZIOAppDefault } from 'zio';

const program = ZIO.serviceWithZIO(Config, config => {
  const apps: Http<
    GoogleBookFinder & ReadingHandler & BookHandler & StaticFile,
    never,
    Request,
    Response
  > = RootRouter.app
    .merge(BookRouter.app)
    .merge(ReadingRouter.app);

  return Server.start(config.app.port, apps);
});

const Main: ZIOAppDefault = {
  run: ZIOAppArgs.provide(
    ConfigLive.layer,
    GoogleSheetsServiceLive.layer,
    BookHandlerLive.layer,
    ReadingHandlerLive.layer,
    StaticFileLive.layer,
    GoogleBookFinderLive.layer
  ).forever
};

export default Main;
