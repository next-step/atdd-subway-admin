package nextstep.subway.station.exception;

public class StationNotFoundException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "역을 찾을 수 없습니다 : ";
    public StationNotFoundException(Long id) {
        super(DEFAULT_MESSAGE + id);
    }
}
