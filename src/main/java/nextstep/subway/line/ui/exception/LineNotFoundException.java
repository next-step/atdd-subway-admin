package nextstep.subway.line.ui.exception;

import java.util.NoSuchElementException;

public class LineNotFoundException extends NoSuchElementException {

    private static String LINE_NOT_FOUND_ERROR = "존재하지 않는 노선 입니다.";

    public LineNotFoundException() {
        super(LINE_NOT_FOUND_ERROR);
    }

    public LineNotFoundException(final String message) {
        super(message);
    }
}
