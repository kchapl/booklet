export class Failure {
  message: string;
  cause: Error | null;

  constructor(message: string, cause: Error | null = null) {
    this.message = message;
    this.cause = cause;
  }

  static fromThrowable(t: Error): Failure {
    return new Failure(t.message, t);
  }

  static fromDecodingException(s: string): Failure {
    return new Failure(`Json decoding exception: ${s}`);
  }
}
