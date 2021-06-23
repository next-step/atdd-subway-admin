package nextstep.subway.exception;

public class NotFoundException extends RuntimeException {

    public static final String MESSAGE = "조회 할 수 없습니다.";

    @Override
    public String getMessage() {
        return MESSAGE;
    }
}
