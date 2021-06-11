package nextstep.subway.exception;

public class ConflictException extends RuntimeException {

    public ConflictException() {
    }

    private final String message = "이미 존재하는 Line 입니다";

    @Override
    public String getMessage() {
        return message;
    }

}
