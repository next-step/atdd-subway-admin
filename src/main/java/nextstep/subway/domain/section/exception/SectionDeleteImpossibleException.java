package nextstep.subway.domain.section.exception;

import nextstep.subway.global.error.ErrorCode;
import nextstep.subway.global.error.exception.BusinessException;

public class SectionDeleteImpossibleException extends BusinessException {

    public SectionDeleteImpossibleException() {
        super(ErrorCode.SECTION_ONE);
    }
}
