package nextstep.subway.common.exception;

import javax.persistence.EntityNotFoundException;

public class NoResultDataException extends EntityNotFoundException {
    public static final String MESSAGE_NO_RESULT_DATA = "데이터가 존재하지 않습니다.";
    public NoResultDataException() {
        super(MESSAGE_NO_RESULT_DATA);
    }
}
