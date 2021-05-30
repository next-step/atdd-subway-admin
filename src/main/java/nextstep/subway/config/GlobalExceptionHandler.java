package nextstep.subway.config;

import nextstep.subway.common.ErrorResponse;
import nextstep.subway.line.exception.NotFoundLineException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse constraintViolationExceptionHandler(DataIntegrityViolationException e) {
        log.error(e.getMessage(), e);
        return ErrorResponse.of("이미 생성된 데이터입니다.");
    }

    @ExceptionHandler(NotFoundLineException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse constraintViolationExceptionHandler(NotFoundLineException e) {
        log.error(e.getMessage(), e);
        return ErrorResponse.of(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse exceptionHandler(Exception e) {
        log.error(e.getMessage(), e);
        return ErrorResponse.of("알 수 없는 오류가 발생했습니다");
    }
}
