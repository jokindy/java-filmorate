create table IF NOT EXISTS FILMS
(
    FILM_ID      INTEGER auto_increment
        primary key,
    NAME         CHARACTER VARYING(100),
    DESCRIPTION  CHARACTER VARYING(200),
    RELEASE_DATE DATE,
    DURATION     INTEGER,
    RATE         INTEGER,
    MPA_ID       INTEGER
);

create table IF NOT EXISTS FILM_GENRES
(
    FILM_ID  INTEGER,
    GENRE_ID INTEGER not null,
    constraint FILM_GENRES_PK
        primary key (GENRE_ID),
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
    USER_ID  INTEGER auto_increment
        primary key,
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

