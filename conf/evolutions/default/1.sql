# --- !Ups

CREATE TABLE books (
  id     SERIAL PRIMARY KEY,
  author VARCHAR(255) NOT NULL,
  title  VARCHAR(255) NOT NULL
);

# --- !Downs

DROP TABLE books;
