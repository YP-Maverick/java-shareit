package ru.practicum.gateway.exception.handler;

import io.micrometer.core.instrument.config.validate.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.gateway.booking.BookingController;
import ru.practicum.gateway.item.ItemController;
import ru.practicum.gateway.exception.BadRequestException;
import ru.practicum.gateway.exception.UnsupportedStatusException;
import ru.practicum.gateway.user.UserController;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RestControllerAdvice(assignableTypes = {
        //UserController.class,
        ItemController.class,
        BookingController.class
        //ItemRequestController.class
        })
public class GatewayExceptionHandler {
    private void log(Throwable e) {
        log.error("Исключение {}: {}", e, e.getMessage());
    }

    @ExceptionHandler({ValidationException.class, BadRequestException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidate(Exception e) {
        log(e);
        return Map.of("error", e.getClass().getSimpleName(),
                "errorMessage", e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValid(final MethodArgumentNotValidException e) {
        log(e);
        List<String> details = new ArrayList<>();
        for (ObjectError error : e.getBindingResult().getAllErrors()) {
            details.add(error.getDefaultMessage());
        }
        return Map.of("error", "Validation exception",
                "errorMessage", details.get(0));
    }

    @ExceptionHandler(UnsupportedStatusException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleUnsupport(final UnsupportedStatusException e) {
        log(e);
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleOtherExc(final Exception e) {
        log(e);
        return Map.of("error", "Unexpected error",
                "errorMessage", e.getMessage());
    }
}