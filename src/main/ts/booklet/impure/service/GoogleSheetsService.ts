import { google, sheets_v4 } from 'googleapis';
import { OAuth2Client } from 'google-auth-library';
import { Failure } from '../../pure/Failure';
import { BookData, BookId } from '../../pure/model/BookData';
import { ReadingData, ReadingId } from '../../pure/model/ReadingData';

export interface GoogleSheetsService {
  createSheet(userId: string): Promise<string>;
  fetchSheetId(userId: string): Promise<string | null>;
  insertBook(userId: string, data: BookData): Promise<void>;
  updateBook(userId: string, id: BookId, data: BookData): Promise<void>;
  deleteBook(userId: string, id: BookId): Promise<void>;
  insertReading(userId: string, data: ReadingData): Promise<void>;
  updateReading(userId: string, id: ReadingId, data: ReadingData): Promise<void>;
  deleteReading(userId: string, id: ReadingId): Promise<void>;
}

export class GoogleSheetsServiceLive implements GoogleSheetsService {
  private sheets: sheets_v4.Sheets;
  private authClient: OAuth2Client;

  constructor(clientId: string, clientSecret: string) {
    this.authClient = new google.auth.OAuth2(clientId, clientSecret);
    this.sheets = google.sheets({ version: 'v4', auth: this.authClient });
  }

  async createSheet(userId: string): Promise<string> {
    try {
      const response = await this.sheets.spreadsheets.create({
        requestBody: {
          properties: {
            title: `ReadingHistory_${userId}`,
          },
        },
      });
      return response.data.spreadsheetId!;
    } catch (error) {
      throw new Failure('Failed to create Google sheet', error);
    }
  }

  async fetchSheetId(userId: string): Promise<string | null> {
    // Implement the logic to fetch the Google sheet ID for the user
    return null;
  }

  async insertBook(userId: string, data: BookData): Promise<void> {
    // Implement the logic to insert a book record into the user's Google sheet
  }

  async updateBook(userId: string, id: BookId, data: BookData): Promise<void> {
    // Implement the logic to update a book record in the user's Google sheet
  }

  async deleteBook(userId: string, id: BookId): Promise<void> {
    // Implement the logic to delete a book record from the user's Google sheet
  }

  async insertReading(userId: string, data: ReadingData): Promise<void> {
    // Implement the logic to insert a reading record into the user's Google sheet
  }

  async updateReading(userId: string, id: ReadingId, data: ReadingData): Promise<void> {
    // Implement the logic to update a reading record in the user's Google sheet
  }

  async deleteReading(userId: string, id: ReadingId): Promise<void> {
    // Implement the logic to delete a reading record from the user's Google sheet
  }
}
