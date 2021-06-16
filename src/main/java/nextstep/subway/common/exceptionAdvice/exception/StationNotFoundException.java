package nextstep.subway.common.exceptionAdvice.exception;

public class StationNotFoundException extends RuntimeException {
    private static final String MESSAGE = "%d 지하철역은 존재하지 않는 지하철역 입니다.";

    public StationNotFoundException(Long id) {
        super(String.format(MESSAGE, id));
    }
}
