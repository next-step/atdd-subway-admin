package nextstep.subway.Exception;

import javassist.NotFoundException;

public class NotFoundLineException extends NotFoundException {
    private static final String MESSAGE = "해당하는 지하철노선을 찾을 수 없습니다. (id: %d)";

    public NotFoundLineException(Long lineId) {
        super(String.format(MESSAGE, lineId));
    }
}
