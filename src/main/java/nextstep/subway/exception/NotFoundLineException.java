package nextstep.subway.exception;

import javassist.NotFoundException;

public class NotFoundLineException extends NotFoundException {
    private static final String message = "지하철노선(%d)이 없습니다.";

    public NotFoundLineException(Long id) {
        super(String.format(message, id));
    }
}
