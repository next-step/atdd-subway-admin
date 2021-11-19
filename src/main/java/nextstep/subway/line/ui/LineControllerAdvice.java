package nextstep.subway.line.ui;

import nextstep.subway.common.ui.ErrorResponse;
import nextstep.subway.line.exception.DuplicateLineNameException;
import nextstep.subway.line.exception.LineNotFoundException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class LineControllerAdvice {

    @ExceptionHandler(DuplicateLineNameException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateLineNameException(DuplicateLineNameException error) {
        return new ResponseEntity<>(new ErrorResponse(error), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException error) {
        String message = error.getBindingResult()
                .getFieldErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(", "));
        return new ResponseEntity<>(new ErrorResponse(message), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(LineNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleLineNotFoundException(LineNotFoundException error) {
        return new ResponseEntity<>(new ErrorResponse(error), HttpStatus.NOT_FOUND);
    }
}
