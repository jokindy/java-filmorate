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

    public String add(T model) throws ValidationException {
        if (map.containsValue(model)) {
            throw new ValidationException("This " + className.toLowerCase() + " is already added");
        }
        id++;
        model.setId(id);
        map.put(id, model);
        return className + " id: " + id + " added.";
    }

    public String put(T model) throws ValidationException {
        int modelId = model.getId();
        if (map.containsKey(modelId) && map.containsValue(model)) {
            throw new ValidationException(className + " id: " + modelId + " is the same");
        } else if (map.containsKey(modelId)) {
            map.replace(modelId, model);
            return className + " id: " + modelId + " updated";
        } else {
            return add(model);
        }
    }

    public Map<Integer, T> getMap() {
        return map;
    }
}
