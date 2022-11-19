package nextstep.subway.ui;

import nextstep.subway.dto.ErrorResponse;
import nextstep.subway.exception.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class SubwayControllerAdvice {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> subwayException(BadRequestException exception) {
        return ResponseEntity.badRequest().body(new ErrorResponse(exception));
    }
}
