package nextstep.subway.line.domain;

import nextstep.subway.global.BusinessException;

public class SectionRemoveFailedException extends BusinessException {

    public SectionRemoveFailedException(String message) {
        super(message);
    }
}
