# --- !Ups

CREATE TABLE Books (
  id     VARCHAR(20) PRIMARY KEY,
  author VARCHAR(255) NOT NULL,
  title  VARCHAR(255) NOT NULL
);

INSERT INTO Books VALUES ('1', 'a', 't');


# --- !Downs

DROP TABLE Books;
