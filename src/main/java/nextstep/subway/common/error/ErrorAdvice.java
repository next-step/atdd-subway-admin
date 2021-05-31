package nextstep.subway.common.error;

import nextstep.subway.line.application.LineDuplicatedException;
import nextstep.subway.line.application.LineNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
@ResponseBody
public class ErrorAdvice {
    private static final String LINE_ALREADY_EXISTED = "이미 존재하는 노선을 생성할 수 없습니다";
    private static final String LINE_NOT_FOUND = "존재하지 않은 노선입니다.";

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(LineDuplicatedException.class)
    public ErrorResponse handleLineDuplicated() {
        return new ErrorResponse(LINE_ALREADY_EXISTED);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(LineNotFoundException.class)
    public ErrorResponse handleLineNotFound() {
        return new ErrorResponse(LINE_NOT_FOUND);
    }
}
