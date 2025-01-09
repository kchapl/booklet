import { Http, Request, Response } from 'some-http-library';
import { BookHandler } from '../service/BookHandler';
import { Failure } from '../../pure/Failure';
import { CustomResponse } from '../../pure/http/CustomResponse';

export const BookRouter = {
  app: Http.collectZIO<Request, Response>({
    'GET /books': async (req: Request) => {
      const userId = req.headers['user_id'];
      if (!userId) {
        return CustomResponse.badRequest('Missing user ID');
      }
      try {
        const response = await BookHandler.fetchAll(userId);
        return response;
      } catch (error) {
        return CustomResponse.serverFailure(Failure.fromThrowable(error));
      }
    },
    'GET /books/:bookId': async (req: Request) => {
      const userId = req.headers['user_id'];
      const bookId = req.params.bookId;
      if (!userId || !bookId) {
        return CustomResponse.badRequest('Missing user ID or book ID');
      }
      try {
        const response = await BookHandler.fetch(bookId, userId);
        return response;
      } catch (error) {
        return CustomResponse.serverFailure(Failure.fromThrowable(error));
      }
    },
    'POST /books': async (req: Request) => {
      const userId = req.headers['user_id'];
      if (!userId) {
        return CustomResponse.badRequest('Missing user ID');
      }
      try {
        const response = await BookHandler.create(req, userId);
        return response;
      } catch (error) {
        return CustomResponse.serverFailure(Failure.fromThrowable(error));
      }
    },
    'PATCH /books/:bookId': async (req: Request) => {
      const userId = req.headers['user_id'];
      const bookId = req.params.bookId;
      if (!userId || !bookId) {
        return CustomResponse.badRequest('Missing user ID or book ID');
      }
      try {
        const response = await BookHandler.update(bookId, req, userId);
        return response;
      } catch (error) {
        return CustomResponse.serverFailure(Failure.fromThrowable(error));
      }
    },
    'DELETE /books/:bookId': async (req: Request) => {
      const userId = req.headers['user_id'];
      const bookId = req.params.bookId;
      if (!userId || !bookId) {
        return CustomResponse.badRequest('Missing user ID or book ID');
      }
      try {
        const response = await BookHandler.delete(bookId, userId);
        return response;
      } catch (error) {
        return CustomResponse.serverFailure(Failure.fromThrowable(error));
      }
    }
  })
};
