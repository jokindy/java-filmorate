create table IF NOT EXISTS FILMS
(
    FILM_ID      INTEGER auto_increment primary key,
    NAME         CHARACTER VARYING(100),
    DESCRIPTION  CHARACTER VARYING(200),
    RELEASE_DATE DATE,
    DURATION     INTEGER,
    RATE         INTEGER,
    MPA_ID       INTEGER,
    DIRECTOR_ID  INTEGER
);

create table IF NOT EXISTS FILM_GENRES
(
    FILM_ID  INTEGER,
    GENRE_ID INTEGER not null,
    constraint FILM_ID
        foreign key (FILM_ID) references FILMS
            on delete cascade
);

create table IF NOT EXISTS GENRES
(
    GENRE_ID INTEGER,
    NAME     CHARACTER VARYING
);

create table IF NOT EXISTS MPA
(
    MPA_ID INTEGER not null,
    NAME   CHARACTER VARYING
);


create table IF NOT EXISTS USERS
(
    USER_ID  INTEGER auto_increment primary key,
    EMAIL    CHARACTER VARYING(50) not null,
    LOGIN    CHARACTER VARYING(50),
    NAME     CHARACTER VARYING(50),
    BIRTHDAY DATE
);

CREATE TABLE IF NOT EXISTS USER_LIKES
(
    LIKE_ID integer AUTO_INCREMENT,
    FILM_ID INTEGER,
    USER_ID INTEGER,
    primary key (LIKE_ID, FILM_ID, USER_ID),
    constraint USER_LIKES_FILMS_FILM_ID_FK
        foreign key (FILM_ID) references FILMS
            on update cascade on delete cascade,
    constraint USER_LIKES_USERS_USER_ID_FK
        foreign key (USER_ID) references USERS
            on update cascade on delete cascade
);

CREATE TABLE IF NOT EXISTS FRIENDS
(
    FRIEND_ID INTEGER AUTO_INCREMENT,
    USER1_ID  INTEGER not null,
    USER2_ID  INTEGER not null,
    STATUS    BOOLEAN,
    primary key (FRIEND_ID, USER1_ID, USER2_ID),
    constraint FRIENDS_USERS_USER_ID_FK
        foreign key (USER1_ID) references USERS
            on delete cascade,
    constraint FRIENDS_USERS_USER_ID_FK_2
        foreign key (USER2_ID) references USERS
            on delete cascade
);

CREATE TABLE IF NOT EXISTS EVENTS
(
    event_id   int primary key auto_increment,
    timestamp  timestamp,
    user_Id    int references USERS,
    event_Type enum ('LIKE', 'REVIEW', 'FRIEND'),
    operation  enum ('REMOVE', 'ADD', 'UPDATE'),
    entity_Id  int
);

create table IF NOT EXISTS DIRECTORS
(
    DIRECTOR_ID INTEGER auto_increment,
    NAME        CHARACTER VARYING,
    constraint DIRECTORS_PK
        primary key (DIRECTOR_ID)

);

merge into mpa key (mpa_id)
    values (1, 'G'),
           (2, 'PG'),
           (3, 'PG-13'),
           (4, 'R'),
           (5, 'NC-17');
merge into GENRES key (GENRE_ID)
    values (1, 'Комедия'),
           (2, 'Драма'),
           (3, 'Мультфильм'),
           (4, 'Ужасы'),
           (5, 'Триллер'),
           (6, 'Детектив');

CREATE TABLE IF NOT EXISTS REVIEWS
(
    REVIEW_ID   INTEGER AUTO_INCREMENT PRIMARY KEY,
    CONTENT     VARCHAR(255),
    IS_POSITIVE BOOLEAN,
    USER_ID     INTEGER,
    FILM_ID     INTEGER,
    USEFUL      INTEGER,
    constraint REVIEWS_USERS_ID_FK
        foreign key (USER_ID) references USERS
            on delete cascade,
    constraint REVIEWS_FILMS_ID_FK
        foreign key (FILM_ID) references FILMS
            on delete cascade
);

CREATE TABLE IF NOT EXISTS REVIEWS_USEFUL
(
    USEFUL_ID INTEGER AUTO_INCREMENT PRIMARY KEY,
    REVIEW_ID INTEGER,
    USER_ID   INTEGER,
    USEFUL    INTEGER,
    constraint REVIEWS_USEFUL_REVIEWS_ID_FK
        foreign key (REVIEW_ID) references REVIEWS
            on delete cascade,
    constraint REVIEWS_USEFUL_USER_ID_FK
        foreign key (USER_ID) references USERS
            on delete cascade
);