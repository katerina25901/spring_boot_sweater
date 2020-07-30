package com.example.sweater.controller;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class ControllerUtils {
    static Map<String, String> getErrors(BindingResult bindingResult) {

//используем коллекторы и в мэп. в качестве ключа будем использовать филдэрор имя филда + эррор
        //а значение - это FieldError::getDefaultMessage
        Collector<FieldError, ?, Map<String, String>> collector = Collectors.toMap(
                fieldError -> fieldError.getField() + "Error",
                FieldError::getDefaultMessage
        );

//получаем лист с ошибками и через стрим апи преобразуем его в мэп с ошибками (идем в верхний кооммент)
        return bindingResult.getFieldErrors().stream().collect(collector);
    }
}
