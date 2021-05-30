package nextstep.subway.common.error;

import nextstep.subway.line.application.LineDuplicatedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
@ResponseBody
public class ErrorAdvice {

    public static final String LINE_ALREADY_EXISTED = "이미 존재하는 노선을 생성할 수 없습니다";

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(LineDuplicatedException.class)
    public ErrorResponse handleLineDuplicated() {
        return new ErrorResponse(LINE_ALREADY_EXISTED);
    }
}
