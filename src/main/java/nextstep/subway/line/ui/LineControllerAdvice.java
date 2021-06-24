package nextstep.subway.line.ui;

import nextstep.subway.line.dto.LineExceptionResponse;
import nextstep.subway.line.exception.LineNotFoundException;
import nextstep.subway.station.exception.StationNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@ResponseBody
@ControllerAdvice(basePackages = "nextstep.subway.line.ui")
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

    @ExceptionHandler({LineNotFoundException.class, StationNotFoundException.class})
    @ResponseStatus(NOT_FOUND)
    public void entityNotFoundException(EntityNotFoundException e) {
        e.printStackTrace();
    }
}
