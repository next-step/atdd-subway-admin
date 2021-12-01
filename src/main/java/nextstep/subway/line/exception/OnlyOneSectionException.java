package nextstep.subway.line.exception;

import nextstep.subway.common.exception.ServiceException;

public class OnlyOneSectionException extends ServiceException {
    public static final String MESSAGE_ONLY_ONE_SECTION = "해당 라인에는 하나의 구간밖에 없습니다.";
    public OnlyOneSectionException() {
        super(MESSAGE_ONLY_ONE_SECTION);
    }

    public OnlyOneSectionException(String message) {
        super(message);
    }
}
