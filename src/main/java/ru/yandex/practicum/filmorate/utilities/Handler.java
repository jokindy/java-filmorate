package ru.yandex.practicum.filmorate.utilities;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.AbstractModel;

@Slf4j
public class Handler<T extends AbstractModel> {

    public String handle(RequestMethod method, Storage<T> storage, T t) {
        try {
            if (t.validate()) {
                throw new ValidationException("Something wrong. Check the data.");
            }
            switch (method) {
                case POST: {
                    String s = storage.add(t);
                    log.info(s);
                    return s;
                }
                case PUT: {
                    String s = storage.put(t);
                    log.info(s);
                    return s;
                }
                default:
                    return "Unchecked method";
            }
        } catch (ValidationException e) {
            log.warn(e.getMessage());
            return e.getMessage();
        }
    }
}
