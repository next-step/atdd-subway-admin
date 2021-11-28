package nextstep.subway.line.exception;

import javax.persistence.EntityNotFoundException;

public class SectionNotFoundException extends EntityNotFoundException {
    public static final String SECTION_NOT_FOUND_EXCEPTION = "해당_구간_영역이_존재하지 않습니다.";

    public SectionNotFoundException(String message) {
        super(message);
    }

    public SectionNotFoundException() {
        super(SECTION_NOT_FOUND_EXCEPTION);
    }
}
