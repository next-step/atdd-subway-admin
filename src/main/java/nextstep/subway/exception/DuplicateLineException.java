package nextstep.subway.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class DuplicateLineException extends BusinessException {

    private static final String ERROR_MESSAGE = "이미 존재하는 사용자 입니다";

    public DuplicateLineException() {
        super(ERROR_MESSAGE);
    }
}
