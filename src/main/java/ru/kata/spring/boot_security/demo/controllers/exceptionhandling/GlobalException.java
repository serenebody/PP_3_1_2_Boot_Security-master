package ru.kata.spring.boot_security.demo.controllers.exceptionhandling;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.kata.spring.boot_security.demo.exception.ExceptionInfo;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalException{

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionInfo> handleValidationException(MethodArgumentNotValidException ex) {
        String errors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining("; "));
        return new ResponseEntity<>(new ExceptionInfo(errors), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ExceptionInfo> handleUserNotFoundException(UsernameNotFoundException ex) {
        return new ResponseEntity<>(new ExceptionInfo(ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ExceptionInfo> handleAccessDeniedException(AccessDeniedException ex) {
        return new ResponseEntity<>(new ExceptionInfo("Доступ запрещен: " + ex.getMessage()), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionInfo> handleGenericException(Exception ex) {
        return new ResponseEntity<>(new ExceptionInfo("Ошибка: " + ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
