package nextstep.subway.line.ui;

import nextstep.subway.line.dto.LineExceptionResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;

@ControllerAdvice(basePackages = "nextstep.subway.line.ui")
public class LineControllerAdvice {

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public LineExceptionResponse dataIntegrityViolationException(HttpServletRequest request, DataIntegrityViolationException e) {
        e.printStackTrace();
        String exceptionMessage = e.getMessage();
        LineExceptionResponse response = null;
        if (exceptionMessage != null && exceptionMessage.contains("Unique index or primary key violation")) {
            response = new LineExceptionResponse("요청하신 이름이 이미 존재하여 처리할 수 없습니다.");
        }
        return response;
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public void entityNotFoundException(EntityNotFoundException e) {
        e.printStackTrace();
    }
}
