package nextstep.subway.exception;

public class NotExistStationException extends RuntimeException {

    private final String message = "요청한 Station 이 없습니다";

    @Override
    public String getMessage() {
        return message;
    }
}
