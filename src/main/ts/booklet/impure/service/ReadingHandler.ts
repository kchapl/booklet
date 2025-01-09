import { Failure } from "../../pure/Failure";
import { ReadingData, ReadingId } from "../../pure/model/ReadingData";
import { GoogleSheetsService } from "./GoogleSheetsService";
import { ReadingView } from "../../pure/views/ReadingView";
import { Query } from "../../pure/http/Query";
import { CustomResponse } from "../../pure/http/CustomResponse";
import { Request, Response } from "express";

export interface ReadingHandler {
  fetchAll(userId: string): Promise<Response>;
  fetch(readingId: string, userId: string): Promise<Response>;
  create(request: Request, userId: string): Promise<Response>;
  update(readingId: string, request: Request, userId: string): Promise<Response>;
  delete(readingId: string, userId: string): Promise<Response>;
}

export class ReadingHandlerLive implements ReadingHandler {
  private googleSheetsService: GoogleSheetsService;

  constructor(googleSheetsService: GoogleSheetsService) {
    this.googleSheetsService = googleSheetsService;
  }

  async fetchAll(userId: string): Promise<Response> {
    try {
      const readings = await this.googleSheetsService.fetchAllReadings(userId);
      return CustomResponse.ok(ReadingView.list(readings));
    } catch (error) {
      return CustomResponse.serverFailure(Failure.fromThrowable(error));
    }
  }

  async fetch(readingId: string, userId: string): Promise<Response> {
    try {
      const id = this.toReadingId(readingId);
      const reading = await this.googleSheetsService.fetchReading(id, userId);
      if (reading) {
        return CustomResponse.ok(ReadingView.list([reading]));
      } else {
        return CustomResponse.notFound(readingId);
      }
    } catch (error) {
      return CustomResponse.serverFailure(Failure.fromThrowable(error));
    }
  }

  async create(request: Request, userId: string): Promise<Response> {
    try {
      const requestQry = Query.fromRequest(request);
      const readingData = ReadingData.completeFromHttpQuery(requestQry);
      if (readingData) {
        await this.googleSheetsService.insertReading(userId, readingData);
        return CustomResponse.seeOther("/readings");
      } else {
        return CustomResponse.badRequest(requestQry.toString());
      }
    } catch (error) {
      return CustomResponse.serverFailure(Failure.fromThrowable(error));
    }
  }

  async update(readingId: string, request: Request, userId: string): Promise<Response> {
    try {
      const requestQry = Query.fromRequest(request);
      const id = this.toReadingId(readingId);
      const readingData = ReadingData.partialFromHttpQuery(requestQry);
      await this.googleSheetsService.updateReading(userId, id, readingData);
      return CustomResponse.seeOther("/readings");
    } catch (error) {
      return CustomResponse.serverFailure(Failure.fromThrowable(error));
    }
  }

  async delete(readingId: string, userId: string): Promise<Response> {
    try {
      const id = this.toReadingId(readingId);
      await this.googleSheetsService.deleteReading(userId, id);
      return CustomResponse.seeOther("/readings");
    } catch (error) {
      return CustomResponse.serverFailure(Failure.fromThrowable(error));
    }
  }

  private toReadingId(readingId: string): ReadingId {
    const id = parseInt(readingId, 10);
    if (isNaN(id)) {
      throw new Error(`Cannot parse ID ${readingId}`);
    }
    return new ReadingId(id);
  }
}
