package nextstep.subway.exception;

import nextstep.subway.common.ErrorCode;

public class NotAcceptableApiException extends RuntimeException {

    public NotAcceptableApiException(ErrorCode errorCode) {
        super(errorCode.name());
    }
}
