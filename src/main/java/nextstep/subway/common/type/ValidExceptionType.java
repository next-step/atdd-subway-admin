package nextstep.subway.common.type;

public enum ValidExceptionType {
    NOT_VALID_DISTANCE("DISTANCE 0보다 커야합니다.");

    private final String message;

    ValidExceptionType(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
