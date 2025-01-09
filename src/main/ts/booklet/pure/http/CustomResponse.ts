import { Response, Headers, Body, HttpError, Status } from 'some-http-library';
import { Failure } from '../Failure';

export class CustomResponse {
  static ok(body: Body, contentType: string = 'text/html'): Response {
    return new Response({
      headers: new Headers({ 'content-type': contentType }),
      body: body
    });
  }

  static okJs(body: Body): Response {
    return this.ok(body, 'text/javascript');
  }

  static seeOther(path: string): Response {
    return new Response({
      status: Status.SeeOther,
      headers: new Headers({ 'location': path })
    });
  }

  static badRequest(message: string): Response {
    return Response.fromHttpError(new HttpError.BadRequest(message));
  }

  static notFound(path: string): Response {
    return Response.fromHttpError(new HttpError.NotFound(path));
  }

  static serverFailure(failure: Failure): Response {
    return Response.fromHttpError(new HttpError.InternalServerError(failure.message, failure.cause));
  }
}
