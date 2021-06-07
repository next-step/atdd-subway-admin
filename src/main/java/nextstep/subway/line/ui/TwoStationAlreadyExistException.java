package nextstep.subway.line.ui;

public class TwoStationAlreadyExistException extends RuntimeException {

    public TwoStationAlreadyExistException() {
        super("등록하려는 두 역이 이미 노선에 존재합니다.");
    }
}
