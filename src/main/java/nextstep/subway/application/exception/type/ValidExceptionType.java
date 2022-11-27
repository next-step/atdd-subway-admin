package nextstep.subway.application.exception.type;

public enum ValidExceptionType {
    NOT_VALID_DISTANCE("DISTANCE는 0 보다 커야합니다."),
    NOT_VALID_COLOR("COLOR 를 입력해주세요"),
    NOT_VALID_NAME("NAME 를 입력해주세요"),

    MIN_LINE_STATIONS_SIZE("구간이 하나인 노선은 제거할 수 없습니다");
    private final String message;

    ValidExceptionType(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
