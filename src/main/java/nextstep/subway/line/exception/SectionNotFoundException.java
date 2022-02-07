package nextstep.subway.line.exception;

import nextstep.subway.common.exception.EntityNotFoundException;

public class SectionNotFoundException extends EntityNotFoundException {

    public SectionNotFoundException(String message) {
        super(message);
    }
}
