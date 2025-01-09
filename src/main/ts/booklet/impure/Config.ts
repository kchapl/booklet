import { z } from 'zod';

const AppConfig = z.object({
  port: z.number(),
});

const BookLookupConfig = z.object({
  url: z.string(),
  key: z.string(),
  signInClientId: z.string(),
});

const GoogleSheetsConfig = z.object({
  clientId: z.string(),
  clientSecret: z.string(),
});

const Config = z.object({
  app: AppConfig,
  bookLookup: BookLookupConfig,
  googleSheets: GoogleSheetsConfig,
});

type AppConfig = z.infer<typeof AppConfig>;
type BookLookupConfig = z.infer<typeof BookLookupConfig>;
type GoogleSheetsConfig = z.infer<typeof GoogleSheetsConfig>;
type Config = z.infer<typeof Config>;

export { AppConfig, BookLookupConfig, GoogleSheetsConfig, Config };
