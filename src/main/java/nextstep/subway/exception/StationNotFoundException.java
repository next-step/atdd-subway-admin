package nextstep.subway.exception;

public class StationNotFoundException extends BusinessException {

    private static final String ERROR_MESSAGE = "역이 존재하지 않습니다.";

    public StationNotFoundException() {
        super(ERROR_MESSAGE);
    }
}