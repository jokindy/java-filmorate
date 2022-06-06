CREATE TABLE IF NOT EXISTS `Films`
(
    `film_id`      int PRIMARY KEY AUTO_INCREMENT,
    `name`         varchar(100),
    `description`  varchar(200),
    `release_date` date,
    `duration`     int,
    `rate`         int,
    `MPA`          int
);

create table if not exists FILM_GENRES
(
    FILM_ID  INTEGER not null,
    GENRE_ID INTEGER not null,
    primary key (FILM_ID, GENRE_ID),
    constraint FILM_GENRES_FILMS_FILM_ID_FK
        foreign key (FILM_ID) references FILMS
            on update cascade on delete cascade,
    constraint FILM_GENRES_GENRES_GENRE_ID_FK
        foreign key (GENRE_ID) references GENRES
            on update cascade on delete cascade
);

CREATE TABLE IF NOT EXISTS `Genres`
(
    `genre_id` int PRIMARY KEY AUTO_INCREMENT,
    `name`     varchar(100)
);

CREATE TABLE IF NOT EXISTS `User_Likes`
(
    FILM_ID INTEGER,
    USER_ID INTEGER,
    constraint USER_LIKES_FILMS_FILM_ID_FK
        foreign key (FILM_ID) references FILMS
            on delete cascade,
    constraint USER_LIKES_USERS_USER_ID_FK
        foreign key (USER_ID) references USERS
            on delete cascade
);

CREATE TABLE IF NOT EXISTS `Users`
(
    `user_id`  int PRIMARY KEY AUTO_INCREMENT,
    `email`    varchar(50) NOT NULL,
    `login`    varchar(50),
    `name`     varchar(50),
    `birthday` date
);

CREATE TABLE IF NOT EXISTS `Friends`
(
    USER1_ID INTEGER not null,
    USER2_ID INTEGER not null,
    STATUS   BOOLEAN,
    primary key (USER1_ID, USER2_ID),
    constraint FRIENDS_USERS_USER_ID_FK
        foreign key (USER1_ID) references USERS
            on delete cascade,
    constraint FRIENDS_USERS_USER_ID_FK_2
        foreign key (USER2_ID) references USERS
            on delete cascade
);

