# --- !Ups

create table Books (
  id     varchar(20) primary key,
  author varchar(255) not null,
  title  varchar(255) not null
);

create table Readings (
  id        varchar(20) primary key,
  bookId    varchar(20) not null,
  completed date        not null,
  rating    smallint    not null
);

insert into Books values ('1', 'a', 't');

insert into Readings values ('1', '1', '2018-05-10', 3);


# --- !Downs

drop table Readings;

drop table Books;
