package nextstep.subway.application.exception.type;

public enum ValidExceptionType {
    NOT_VALID_DISTANCE("DISTANCE는 0 보다 커야합니다."),
    NOT_VALID_COLOR("COLOR 를 입력해주세요"),
    NOT_VALID_NAME("NAME 를 입력해주세요");
    private final String message;

    ValidExceptionType(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
