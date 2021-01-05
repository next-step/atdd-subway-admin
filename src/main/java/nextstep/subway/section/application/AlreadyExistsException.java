package nextstep.subway.section.application;

import nextstep.subway.section.domain.Section;

public class AlreadyExistsException extends RuntimeException {
    public AlreadyExistsException(Section newSection) {
        super(newSection.toString());
    }
}
