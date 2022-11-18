package nextstep.subway.exception;

public class SameStationException extends IllegalArgumentException {

    public SameStationException() {
        super("상행선과 하행선이 동일한 경우 등록을 할 수 없습니다.");
    }
}
