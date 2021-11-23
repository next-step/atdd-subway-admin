package nextstep.subway.exception;

import nextstep.subway.common.ErrorCode;

public class NotFoundApiException extends RuntimeException {

    public NotFoundApiException(ErrorCode errorCode) {
        super(errorCode.name());
    }
}
