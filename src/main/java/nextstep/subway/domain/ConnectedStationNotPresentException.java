package nextstep.subway.domain;

public class ConnectedStationNotPresentException extends RuntimeException {

    private static final long serialVersionUID = 5102853143965082613L;

    public ConnectedStationNotPresentException() {
        super("연결 할 수 없는 구간 입니다.");
    }
}
