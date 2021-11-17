package nextstep.subway.line.domain;

import nextstep.subway.global.BusinessException;

public class SectionAddFailedException extends BusinessException {

    public SectionAddFailedException(String message) {
        super(message);
    }
}
