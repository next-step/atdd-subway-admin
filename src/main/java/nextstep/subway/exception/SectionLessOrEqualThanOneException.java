package nextstep.subway.exception;

import static nextstep.subway.constant.ExceptionMessages.SECTION_LESS_OR_EQUAL_THAN_ONE;

public class SectionLessOrEqualThanOneException extends RuntimeException {
    public SectionLessOrEqualThanOneException() {
        super(SECTION_LESS_OR_EQUAL_THAN_ONE);
    }
}
