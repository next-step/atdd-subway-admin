package nextstep.subway.line.ui;

public class NoStationInLineException extends RuntimeException {
    public NoStationInLineException() {
        super("삭제하려는 역이 노선에 없습니다");
    }
}
