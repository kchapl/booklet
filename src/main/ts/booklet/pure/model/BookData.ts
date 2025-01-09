import { Isbn } from './Isbn';
import { Author } from './Author';
import { Title } from './Title';
import { Subtitle } from './Subtitle';

export class BookData {
  constructor(
    public isbn: Isbn | null,
    public author: Author | null,
    public title: Title | null,
    public subtitle: Subtitle | null,
    public thumbnail: string | null,
    public smallThumbnail: string | null,
    public userId: string | null
  ) {}

  static completeFromHttpQuery(qry: { [key: string]: string }): BookData | null {
    const isbn = qry['isbn'];
    const author = qry['author'];
    const title = qry['title'];

    if (isbn && author && title) {
      return new BookData(
        new Isbn(isbn),
        new Author(author),
        new Title(title),
        null,
        null,
        null,
        null
      );
    }

    return null;
  }

  static partialFromHttpQuery(qry: { [key: string]: string }): BookData {
    return new BookData(
      qry['isbn'] ? new Isbn(qry['isbn']) : null,
      qry['author'] ? new Author(qry['author']) : null,
      qry['title'] ? new Title(qry['title']) : null,
      null,
      null,
      null,
      null
    );
  }
}
