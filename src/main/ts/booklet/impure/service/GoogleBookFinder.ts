import { Failure } from "../../pure/Failure";
import { BookData } from "../../pure/model/BookData";
import { GoogleBookResult, EmptyGoogleBookResult } from "./GoogleBookModel";
import { HttpClient } from "../http/HttpClient";

export interface GoogleBookFinder {
  findByIsbn(isbn: string, idToken: string): Promise<BookData | null>;
}

export class GoogleBookFinderLive implements GoogleBookFinder {
  private httpClient: HttpClient;

  constructor(httpClient: HttpClient) {
    this.httpClient = httpClient;
  }

  async findByIsbn(isbn: string, idToken: string): Promise<BookData | null> {
    try {
      const response = await this.httpClient.get(
        `https://www.googleapis.com/books/v1/volumes?q=isbn:${isbn.replace(/\D/g, "")}`,
        { Authorization: `Bearer ${idToken}` }
      );

      const responseBody = await response.text();
      const bookResult = JSON.parse(responseBody) as GoogleBookResult;

      if (bookResult.totalItems === 1) {
        return GoogleBookResult.toBook(bookResult);
      } else {
        const emptyResult = JSON.parse(responseBody) as EmptyGoogleBookResult;
        if (emptyResult.totalItems === 0) {
          return null;
        } else {
          throw new Error("Unexpected response format");
        }
      }
    } catch (error) {
      throw new Failure(error.message, error);
    }
  }
}
