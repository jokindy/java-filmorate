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

CREATE TABLE IF NOT EXISTS `Film_Genres`
(
    `film_id`  int,
    `genre_id` int,
    PRIMARY KEY (`film_id`, `genre_id`)
);

CREATE TABLE IF NOT EXISTS `Genres`
(
    `genre_id` int PRIMARY KEY AUTO_INCREMENT,
    `name`     varchar(100)
);

CREATE TABLE IF NOT EXISTS `User_Likes`
(
    `film_id` int,
    `user_id` int
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
    `user1_id` int,
    `user2_id` int,
    `status`   boolean,
    PRIMARY KEY (`user1_id`, `user2_id`)
);

ALTER TABLE `User_Likes`
    ADD FOREIGN KEY (`film_id`) REFERENCES `Films` (`film_id`);

ALTER TABLE `User_Likes`
    ADD FOREIGN KEY (`user_id`) REFERENCES `Users` (`user_id`);

ALTER TABLE `Film_Genres`
    ADD FOREIGN KEY (`film_id`) REFERENCES `Films` (`film_id`);

ALTER TABLE `Film_Genres`
    ADD FOREIGN KEY (`genre_id`) REFERENCES `Genres` (`genre_id`);

ALTER TABLE `Friends`
    ADD FOREIGN KEY (`user1_id`) REFERENCES `Users` (`user_id`);

ALTER TABLE `Friends`
    ADD FOREIGN KEY (`user2_id`) REFERENCES `Users` (`user_id`);