package nextstep.subway.line.exception;

import static nextstep.subway.common.Message.*;

import javax.persistence.EntityNotFoundException;

public class SectionNotFoundException extends EntityNotFoundException {

    public SectionNotFoundException() {
        super(SECTION_NOT_FOUND_EXCEPTION.getMessage());
    }

    public SectionNotFoundException(String message) {
        super(message);
    }

}
