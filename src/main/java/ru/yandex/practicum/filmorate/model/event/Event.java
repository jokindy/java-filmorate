package ru.yandex.practicum.filmorate.model.event;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class Event {
    private final Integer eventId;
    private final Integer userId;
    private final Instant timestamp;
    private final EventType eventType;
    private final Operations operation;
    private final Integer entityId;
}
