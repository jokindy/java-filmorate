package ru.yandex.practicum.filmorate.utilities;

import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.AbstractModel;

import java.util.HashMap;
import java.util.Map;

public class Storage<T extends AbstractModel> {

    private final Map<Integer, T> map;
    private final String className;
    private int id = 0;

    public Storage(String className) {
        this.map = new HashMap<>();
        this.className = className;
    }

    public String add(T t) throws ValidationException {
        if (map.containsValue(t)) {
            throw new ValidationException("This " + className.toLowerCase() + " is already added");
        }
        id++;
        t.setId(id);
        map.put(id, t);
        return className + " id: " + id + " added.";
    }

    public String put(T t) throws ValidationException {
        int tId = t.getId();
        if (map.containsKey(tId) && map.containsValue(t)) {
            throw new ValidationException(className + " id: " + tId + " is the same");
        } else if (map.containsKey(tId)) {
            map.replace(tId, t);
            return className + " id: " + tId + " updated";
        } else {
            return add(t);
        }
    }

    public Map<Integer, T> getMap() {
        return map;
    }
}
