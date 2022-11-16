package nextstep.subway.common.exception;

import java.util.HashMap;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

@org.springframework.web.bind.annotation.RestControllerAdvice
public class RestControllerAdvice {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<HashMap> handleIllegalArgsException(IllegalArgumentException e) {
        HashMap<Object, Object> errorMap = new HashMap<>();
        errorMap.put("errorMessage", e.getMessage());
        return ResponseEntity.badRequest().body(errorMap);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<HashMap> handleException(Exception e) {
        HashMap<Object, Object> errorMap = new HashMap<>();
        errorMap.put("errorMessage", ErrorMessageConstant.NOT_EXISTS_LINE);
        return ResponseEntity.badRequest().body(errorMap);
    }
}
