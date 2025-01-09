import { Http, Request, Response } from 'some-http-library';
import { CustomResponse } from '../../pure/http/CustomResponse';
import { RootView } from '../../pure/views/RootView';

export const RootRouter = {
  app: Http.collect<Request, Response>({
    'GET /': (req: Request) => {
      return CustomResponse.ok(RootView.show().toString());
    },
    'POST /api/authenticate': async (req: Request) => {
      try {
        const body = await req.bodyAsString();
        const data = JSON.parse(body);
        const idToken = data.id_token || '';
        const userId = data.user_id || '';
        // Handle the ID token and user ID (e.g., store in session, validate, etc.)
        return CustomResponse.ok();
      } catch (error) {
        return CustomResponse.badRequest('Invalid JSON');
      }
    }
  })
};
