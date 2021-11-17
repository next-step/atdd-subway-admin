package nextstep.subway.exception;

public class StationNotContainInUpOrDownStation extends BusinessException {

    private static final String ERROR_MESSAGE = "등록하려는 역은 상행역 혹은 하행역에 포함되어야 합니다.";

    public StationNotContainInUpOrDownStation() {
        super(ERROR_MESSAGE);
    }
}
