package nextstep.subway.common.exceptionAdvice.exception;

import nextstep.subway.common.exceptionAdvice.ErrorCode;

public class RemoveSectionException extends RuntimeException {
    public RemoveSectionException(){
        super(ErrorCode.REMOVE_SECTION_EXCEPTION.getErrorMessage());
    }
}
