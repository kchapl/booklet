package booklet.model

case class Book(
    id: Id,
    isbn: Isbn,
    author: Author,
    title: Title,
    subtitle: Option[Subtitle],
    thumbnail: Option[String],
    smallThumbnail: Option[String]
)
