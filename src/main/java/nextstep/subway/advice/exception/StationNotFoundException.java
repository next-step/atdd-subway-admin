package nextstep.subway.advice.exception;

public class StationNotFoundException extends RuntimeException {

    public StationNotFoundException(String message) {
        super(message);
    }

    public StationNotFoundException(Long id) {
        super("존재하는 역이 없습니다 (id : " +id+" )");
    }
}
