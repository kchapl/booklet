import { Http, Request, Response } from 'zhttp/http';
import { GoogleBookFinder } from '../service/GoogleBookFinder';
import { Failure } from '../../pure/Failure';
import { CustomResponse } from '../../pure/http/CustomResponse';

export const BookFinderRouter = {
  app: Http.collectZIO<Request, Response>({
    'GET /books/find': async (req: Request) => {
      const isbn = req.query.isbn;
      const idToken = req.headers['Authorization'];
      if (!isbn || !idToken) {
        return CustomResponse.badRequest('Missing ISBN or ID token');
      }
      try {
        const book = await GoogleBookFinder.findByIsbn(isbn, idToken);
        if (book) {
          return CustomResponse.ok(book);
        } else {
          return CustomResponse.notFound();
        }
      } catch (error) {
        return CustomResponse.serverFailure(Failure.fromThrowable(error));
      }
    }
  })
};
