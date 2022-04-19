package ru.yandex.practicum.filmorate.utilities;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.AbstractModel;

@Slf4j
public class Handler<T extends AbstractModel> {

    public String handle(RequestMethod method, Storage<T> storage, T model) {
        try {
            if (model.validate()) {
                throw new ValidationException("Something wrong. Check the data.");
            }
            switch (method) {
                case POST: {
                    String s = storage.add(model);
                    log.info(s);
                    return s;
                }
                case PUT: {
                    String s = storage.put(model);
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
