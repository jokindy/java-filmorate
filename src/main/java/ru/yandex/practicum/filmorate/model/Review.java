package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
public class Review {

    @EqualsAndHashCode.Exclude
    private int reviewId;

    @NotBlank(message = "Content may not be blank")
    private String content;

    @NotNull
    private boolean isPositive;

    @NotNull
    @Positive
    private int userId;

    @NotNull
    @PositiveOrZero
    private int filmId;

    @NotNull
    @EqualsAndHashCode.Exclude
    private int useful;

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("content", content);
        map.put("is_positive", isPositive);
        map.put("user_id", userId);
        map.put("film_id", filmId);
        map.put("useful", useful);
        return map;
    }
}