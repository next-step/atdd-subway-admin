package nextstep.subway.exception;

public class NoStationException extends RuntimeException {
    public NoStationException(Long id) {
        super(String.format("존재하지 않는 역입니다. station id = %d", id));
    }
}
