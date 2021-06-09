package nextstep.subway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT, reason = "Line의 이름은 겹칠 수 없습니다")
public class ConflictException extends RuntimeException {

    public ConflictException() {
    }

    private final String message = "이미 존재하는 Line 입니다";

    @Override
    public String getMessage() {
        return message;
    }

}
