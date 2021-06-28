package nextstep.subway.line.ui;

import nextstep.subway.line.dto.LineExceptionResponse;
import nextstep.subway.section.exception.BelowZeroDistanceException;
import nextstep.subway.section.exception.UnaddableSectionException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;


@RestControllerAdvice(basePackages = "nextstep.subway.line.ui")
public class LineControllerAdvice {

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(BAD_REQUEST)
    public LineExceptionResponse dataIntegrityViolationException(HttpServletRequest request, DataIntegrityViolationException e) {
        e.printStackTrace();

        String exceptionMessage = e.getMessage();
        LineExceptionResponse response = null;
        if (exceptionMessage != null && exceptionMessage.contains("Unique index or primary key violation")) {
            response = new LineExceptionResponse("요청 하신 이름이 이미 존재 하여 처리할 수 없습니다.");
        }
        return response;
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(NOT_FOUND)
    public void entityNotFoundException(EntityNotFoundException e) {
        e.printStackTrace();
    }

    @ExceptionHandler(UnaddableSectionException.class)
    @ResponseStatus(BAD_REQUEST)
    public void unaddableSectionException(UnaddableSectionException e) {
        e.printStackTrace();
    }

    @ExceptionHandler(BelowZeroDistanceException.class)
    @ResponseStatus(BAD_REQUEST)
    public void belowZeroDistanceException(BelowZeroDistanceException e) {
        e.printStackTrace();
    }
}
