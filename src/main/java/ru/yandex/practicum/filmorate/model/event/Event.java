package ru.yandex.practicum.filmorate.model.event;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Data
@Builder
public class Event {

    private int eventId;
    private final int userId;
    private final Instant timestamp;
    private final EventType eventType;
    private final Operation operation;
    private int entityId;

    public static Event getEvent(int userId, EventType type, Operation operation, int friendshipId) {
        return Event.builder()
                .userId(userId)
                .timestamp(Instant.now())
                .eventType(type)
                .operation(operation)
                .entityId(friendshipId)
                .build();
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("timestamp", timestamp);
        map.put("user_id", userId);
        map.put("event_type", eventType);
        map.put("operation", operation);
        map.put("entity_id", entityId);
        return map;
    }
}
