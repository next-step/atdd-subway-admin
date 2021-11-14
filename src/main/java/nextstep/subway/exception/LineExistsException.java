package nextstep.subway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class LineExistsException extends RuntimeException{

    private final static String msg = "이미 존재하는 역입니다";

    public LineExistsException() {
        super(msg);
    }

    public LineExistsException(String message) {
        super(message);
    }
}
