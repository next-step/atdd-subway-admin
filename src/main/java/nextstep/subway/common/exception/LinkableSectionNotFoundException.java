package nextstep.subway.common.exception;

import java.util.NoSuchElementException;

public class LinkableSectionNotFoundException extends NoSuchElementException {

    private static String LINKABLE_SECTION_NOTFOUND_ERROR = "연결 가능한 구간이 없습니다.";

    public LinkableSectionNotFoundException() {
        super(LINKABLE_SECTION_NOTFOUND_ERROR);
    }

    public LinkableSectionNotFoundException(final String message) {
        super(message);
    }
}
