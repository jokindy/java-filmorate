create table IF NOT EXISTS DIRECTORS
(
    DIRECTOR_ID INTEGER auto_increment primary key,
    NAME        CHARACTER VARYING
);

create table IF NOT EXISTS FILMS
(
    FILM_ID      INTEGER UNIQUE auto_increment PRIMARY KEY,
    NAME         CHARACTER VARYING(100),
    DESCRIPTION  CHARACTER VARYING(200),
    RELEASE_DATE DATE,
    DURATION     INTEGER,
    RATE         NUMERIC(10, 2),
    MPA          VARCHAR,
    DIRECTOR_ID  INTEGER,
    FOREIGN KEY (DIRECTOR_ID) REFERENCES DIRECTORS on delete set null
);

create table IF NOT EXISTS FILM_GENRES
(
    FILM_ID  INTEGER not null,
    GENRE_ID INTEGER not null,
    foreign key (FILM_ID) references FILMS (FILM_ID)
        on update cascade on delete cascade
);

create table IF NOT EXISTS GENRES
(
    GENRE_ID INTEGER,
    NAME     CHARACTER VARYING
);

create table IF NOT EXISTS USERS
(
    USER_ID  INTEGER auto_increment primary key,
    EMAIL    CHARACTER VARYING(50) not null,
    LOGIN    CHARACTER VARYING(50),
    NAME     CHARACTER VARYING(50),
    BIRTHDAY DATE
);

CREATE TABLE IF NOT EXISTS FRIENDS
(
    FRIEND_ID INTEGER UNIQUE AUTO_INCREMENT,
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

CREATE TABLE IF NOT EXISTS USER_LIKES
(
    LIKE_ID INTEGER UNIQUE auto_increment,
    FILM_ID INTEGER not null,
    USER_ID INTEGER not null,
    RATE    INTEGER,
    primary key (LIKE_ID, FILM_ID, USER_ID),
    constraint USER_LIKES_FILMS_FILM_ID_FK
        foreign key (FILM_ID) references FILMS (FILM_ID)
            on update cascade on delete cascade,
    constraint USER_LIKES_USERS_USER_ID_FK
        foreign key (USER_ID) references USERS
            on update cascade on delete cascade
);

CREATE TABLE IF NOT EXISTS EVENTS
(
    EVENT_ID   INTEGER auto_increment primary key,
    TIMESTAMP  TIMESTAMP,
    USER_ID    INTEGER
        references USERS
            on update cascade on delete cascade,
    EVENT_TYPE VARCHAR,
    OPERATION  VARCHAR,
    ENTITY_ID  INTEGER
);



create table IF NOT EXISTS DIRECTORS
(
    DIRECTOR_ID INTEGER auto_increment
        primary key,
    NAME        CHARACTER VARYING
);

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
        foreign key (FILM_ID) references FILMS (FILM_ID)
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

merge into GENRES key (GENRE_ID)
    values (1, 'Комедия'),
           (2, 'Драма'),
           (3, 'Мультфильм'),
           (4, 'Ужасы'),
           (5, 'Триллер'),
           (6, 'Детектив');