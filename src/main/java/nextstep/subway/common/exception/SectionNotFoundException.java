package nextstep.subway.common.exception;

import java.util.NoSuchElementException;

public class SectionNotFoundException extends NoSuchElementException {

    private static String SECTION_NOT_FOUND_ERROR = "제거 할 구간을 찾을 수 없습니다.";

    public SectionNotFoundException() {
        super(SECTION_NOT_FOUND_ERROR);
    }

    public SectionNotFoundException(final String message) {
        super(message);
    }
}
