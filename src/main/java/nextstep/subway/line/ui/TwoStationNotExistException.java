package nextstep.subway.line.ui;

public class TwoStationNotExistException extends RuntimeException {
    public TwoStationNotExistException() {
        super("등록하는 두 역이 노선에 모두 존재하지 않습니다.");
    }
}
