package nextstep.subway.domain.section.exception;

import nextstep.subway.global.error.ErrorCode;
import nextstep.subway.global.error.exception.BusinessException;

public class AlreadyRegisterSectionException extends BusinessException {

    public AlreadyRegisterSectionException() {
        super(ErrorCode.ALREADY_REGISTER_SECTION);
    }
}
