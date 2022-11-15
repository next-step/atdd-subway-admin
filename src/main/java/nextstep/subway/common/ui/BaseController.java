package nextstep.subway.common.ui;

import java.util.HashMap;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BaseController {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<HashMap> handleIllegalArgsException(IllegalArgumentException e) {
        HashMap<Object, Object> errorMap = new HashMap<>();
        errorMap.put("errorMessage", e.getMessage());
        return ResponseEntity.badRequest().body(errorMap);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<HashMap> handleException(Exception e) {
        HashMap<Object, Object> errorMap = new HashMap<>();
        errorMap.put("errorMessage", "오류가 발생하였습니다.");
        return ResponseEntity.badRequest().body(errorMap);
    }
}
