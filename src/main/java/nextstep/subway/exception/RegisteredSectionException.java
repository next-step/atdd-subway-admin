package nextstep.subway.exception;

public class RegisteredSectionException extends RuntimeException {

    public static final String MESSAGE = "이미 등록 된 구간입니다.";

    @Override
    public String getMessage() {
        return MESSAGE;
    }
}
