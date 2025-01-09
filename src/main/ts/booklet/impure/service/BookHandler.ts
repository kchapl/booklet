import { Failure } from "../../pure/Failure";
import { BookData, BookId } from "../../pure/model/BookData";
import { GoogleSheetsService } from "./GoogleSheetsService";
import { BookView } from "../../pure/views/BookView";
import { Query } from "../../pure/http/Query";
import { CustomResponse } from "../../pure/http/CustomResponse";
import { Request, Response } from "express";

export interface BookHandler {
  fetchAll(userId: string): Promise<Response>;
  fetch(bookId: string, userId: string): Promise<Response>;
  create(request: Request, userId: string): Promise<Response>;
  update(bookId: string, request: Request, userId: string): Promise<Response>;
  delete(bookId: string, userId: string): Promise<Response>;
}

export class BookHandlerLive implements BookHandler {
  private googleSheetsService: GoogleSheetsService;

  constructor(googleSheetsService: GoogleSheetsService) {
    this.googleSheetsService = googleSheetsService;
  }

  async fetchAll(userId: string): Promise<Response> {
    try {
      const books = await this.googleSheetsService.fetchAllBooks(userId);
      return CustomResponse.ok(BookView.list(books));
    } catch (error) {
      return CustomResponse.serverFailure(Failure.fromThrowable(error));
    }
  }

  async fetch(bookId: string, userId: string): Promise<Response> {
    try {
      const id = this.toBookId(bookId);
      const book = await this.googleSheetsService.fetchBook(id, userId);
      if (book) {
        return CustomResponse.ok(BookView.list([book]));
      } else {
        return CustomResponse.notFound(bookId);
      }
    } catch (error) {
      return CustomResponse.serverFailure(Failure.fromThrowable(error));
    }
  }

  async create(request: Request, userId: string): Promise<Response> {
    try {
      const requestQry = Query.fromRequest(request);
      const bookData = BookData.completeFromHttpQuery(requestQry);
      if (bookData) {
        await this.googleSheetsService.insertBook(userId, bookData);
        return CustomResponse.seeOther("/books");
      } else {
        return CustomResponse.badRequest(requestQry.toString());
      }
    } catch (error) {
      return CustomResponse.serverFailure(Failure.fromThrowable(error));
    }
  }

  async update(bookId: string, request: Request, userId: string): Promise<Response> {
    try {
      const requestQry = Query.fromRequest(request);
      const id = this.toBookId(bookId);
      const bookData = BookData.partialFromHttpQuery(requestQry);
      await this.googleSheetsService.updateBook(userId, id, bookData);
      return CustomResponse.seeOther("/books");
    } catch (error) {
      return CustomResponse.serverFailure(Failure.fromThrowable(error));
    }
  }

  async delete(bookId: string, userId: string): Promise<Response> {
    try {
      const id = this.toBookId(bookId);
      await this.googleSheetsService.deleteBook(userId, id);
      return CustomResponse.seeOther("/books");
    } catch (error) {
      return CustomResponse.serverFailure(Failure.fromThrowable(error));
    }
  }

  private toBookId(bookId: string): BookId {
    const id = parseInt(bookId, 10);
    if (isNaN(id)) {
      throw new Error(`Cannot parse ID ${bookId}`);
    }
    return new BookId(id);
  }
}
