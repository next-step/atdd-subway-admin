package nextstep.subway.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import nextstep.subway.exception.dto.ErrorResponse;
import nextstep.subway.exception.line.LineAlreadyExistException;
import nextstep.subway.exception.line.NotFoundLineException;

@RestControllerAdvice
public class LineControllerAdvice extends ControllerAdvice {

    @ExceptionHandler(LineAlreadyExistException.class)
    public ResponseEntity<ErrorResponse> lineAlreadyExistException(LineAlreadyExistException e) {
        return getBadResponse(e.getMessage());
    }

    @ExceptionHandler(NotFoundLineException.class)
    public ResponseEntity<ErrorResponse> notFoundLineException(NotFoundLineException e) {
        return getBadResponse(e.getMessage());
    }

}
