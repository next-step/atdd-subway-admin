package nextstep.subway.exception;

public class NotFoundStationException extends IllegalArgumentException {
    private static final String message = "지하철역(%d)이 없습니다.";

    public NotFoundStationException(Long id) {
        super(String.format(message, id));
    }
}
