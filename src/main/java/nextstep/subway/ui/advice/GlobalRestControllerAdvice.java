package nextstep.subway.ui.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalRestControllerAdvice {

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Map<String, String>> noSuchElementException(Exception e) {
        // TODO Error Response Dto 추가가 필요해보임
        Map<String, String> errors = new HashMap<>();
        errors.put("message", e.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errors);
    }
}
