import { BookData } from "../../pure/model/BookData";
import { Isbn } from "../../pure/model/Isbn";
import { Author } from "../../pure/model/Author";
import { Title } from "../../pure/model/Title";

export interface Identifier {
  type: string;
  identifier: string;
}

export interface ImageLinks {
  smallThumbnail: string;
  thumbnail: string;
}

export interface GoogleBook {
  title: string;
  subtitle?: string;
  authors: string[];
  publisher?: string;
  publishedDate: string;
  description?: string;
  industryIdentifiers: Identifier[];
  categories: string[];
  imageLinks?: ImageLinks;
}

export interface GoogleBookItem {
  volumeInfo: GoogleBook;
}

export interface GoogleBookResult {
  totalItems: number;
  items: GoogleBookItem[];
}

export interface EmptyGoogleBookResult {
  totalItems: number;
  kind: string;
}

export namespace GoogleBookResult {
  export function toBook(result: GoogleBookResult): BookData | null {
    if (result.totalItems === 1) {
      const item = result.items[0];
      const info = item.volumeInfo;
      const author = info.authors[0];

      return {
        isbn: info.industryIdentifiers.find((id) => id.type === "ISBN_13")?.identifier || "Unknown",
        author: author,
        title: info.title,
        subtitle: info.subtitle,
        thumbnail: info.imageLinks?.thumbnail,
        smallThumbnail: info.imageLinks?.smallThumbnail,
        userId: null
      };
    } else {
      return null;
    }
  }
}
