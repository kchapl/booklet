export class Query {
  static fromRequest(request: Request): Map<string, string> {
    const url = new URL(request.url);
    const params = new URLSearchParams(url.search);
    const queryMap = new Map<string, string>();

    params.forEach((value, key) => {
      queryMap.set(key, value);
    });

    return queryMap;
  }
}
