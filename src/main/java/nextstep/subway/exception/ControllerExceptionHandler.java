package nextstep.subway.exception;

import nextstep.subway.common.BaseResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<BaseResponse> handleIllegalArgsException() {
        return ResponseEntity.badRequest()
                .build();
    }

    @ExceptionHandler(CannotFindEntityException.class)
    public ResponseEntity<BaseResponse> handleCannotFindEntityException() {
        return ResponseEntity.badRequest()
                .build();
    }
}
