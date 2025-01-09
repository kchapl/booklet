import { Isbn } from './Isbn';
import { Author } from './Author';
import { Title } from './Title';
import { Subtitle } from './Subtitle';

export class Book {
  constructor(
    public isbn: Isbn,
    public author: Author,
    public title: Title,
    public subtitle: Subtitle,
    public thumbnail: string,
    public smallThumbnail: string
  ) {}
}
