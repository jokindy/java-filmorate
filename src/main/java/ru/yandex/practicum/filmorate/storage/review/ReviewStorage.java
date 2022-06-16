package ru.yandex.practicum.filmorate.storage.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;

public interface ReviewStorage {

    void addReview(Review review);

    void putReview(Review review);

    void deleteReview(int reviewId);

    Review getReviewById(int reviewId);

    Collection<Review> getReviewsByFilmIdAndCount(int filmId, int count);

    void putUseful(int reviewId, int userId, int value);

    void deleteUseful(int reviewId, int userId, int value);

    boolean isContains(int reviewId);
}