import { Http, Request, Response } from 'some-http-library';
import { ReadingHandler } from '../service/ReadingHandler';
import { Failure } from '../../pure/Failure';
import { CustomResponse } from '../../pure/http/CustomResponse';

export const ReadingRouter = {
  app: Http.collectZIO<Request, Response>({
    'GET /readings': async (req: Request) => {
      const userId = req.headers['user_id'];
      if (!userId) {
        return CustomResponse.badRequest('Missing user ID');
      }
      try {
        const response = await ReadingHandler.fetchAll(userId);
        return response;
      } catch (error) {
        return CustomResponse.serverFailure(Failure.fromThrowable(error));
      }
    },
    'GET /readings/:readingId': async (req: Request) => {
      const userId = req.headers['user_id'];
      const readingId = req.params.readingId;
      if (!userId || !readingId) {
        return CustomResponse.badRequest('Missing user ID or reading ID');
      }
      try {
        const response = await ReadingHandler.fetch(readingId, userId);
        return response;
      } catch (error) {
        return CustomResponse.serverFailure(Failure.fromThrowable(error));
      }
    },
    'POST /readings': async (req: Request) => {
      const userId = req.headers['user_id'];
      if (!userId) {
        return CustomResponse.badRequest('Missing user ID');
      }
      try {
        const response = await ReadingHandler.create(req, userId);
        return response;
      } catch (error) {
        return CustomResponse.serverFailure(Failure.fromThrowable(error));
      }
    },
    'PATCH /readings/:readingId': async (req: Request) => {
      const userId = req.headers['user_id'];
      const readingId = req.params.readingId;
      if (!userId || !readingId) {
        return CustomResponse.badRequest('Missing user ID or reading ID');
      }
      try {
        const response = await ReadingHandler.update(readingId, req, userId);
        return response;
      } catch (error) {
        return CustomResponse.serverFailure(Failure.fromThrowable(error));
      }
    },
    'DELETE /readings/:readingId': async (req: Request) => {
      const userId = req.headers['user_id'];
      const readingId = req.params.readingId;
      if (!userId || !readingId) {
        return CustomResponse.badRequest('Missing user ID or reading ID');
      }
      try {
        const response = await ReadingHandler.delete(readingId, userId);
        return response;
      } catch (error) {
        return CustomResponse.serverFailure(Failure.fromThrowable(error));
      }
    }
  })
};
