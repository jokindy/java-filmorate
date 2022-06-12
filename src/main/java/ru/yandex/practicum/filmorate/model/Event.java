package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class Event {
    Integer eventId;
    Integer userId;
    Instant timestamp;
    EventType eventType;
    Operations operation;
    Integer entityId;
}
