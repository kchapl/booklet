DROP TABLE IF EXISTS readings;
DROP TABLE IF EXISTS books;

CREATE TABLE books
(
    id     SERIAL PRIMARY KEY,
    author TEXT NOT NULL,
    title  TEXT NOT NULL
);

CREATE TABLE readings
(
    id        SERIAL PRIMARY KEY,
    book_id   INT,
    completed DATE,
    rating    SMALLINT,
    CONSTRAINT fk_book
        FOREIGN KEY (book_id)
            REFERENCES books (id)
);
