package nextstep.subway.line.exception;

public class StationNotExistException extends IllegalArgumentException {

    public StationNotExistException() {
        super("노선에 등록되어 있지 않은 역 입니다.");
    }
}
