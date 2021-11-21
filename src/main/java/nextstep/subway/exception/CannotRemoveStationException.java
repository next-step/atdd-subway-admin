package nextstep.subway.exception;

public class CannotRemoveStationException extends BusinessException {

    private static final String ERROR_MESSAGE = "구간이 하나인 경우 역을 삭제할 수 없습니다.";

    public CannotRemoveStationException() {
        super(ERROR_MESSAGE);
    }
}
