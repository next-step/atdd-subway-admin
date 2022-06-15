package nextstep.subway.exception;

import javassist.NotFoundException;

public class NotFoundStationException extends NotFoundException {
    private static final String message = "지하철역(%d)이 없습니다.";

    public NotFoundStationException(Long id) {
        super(String.format(message, id));
    }
}
