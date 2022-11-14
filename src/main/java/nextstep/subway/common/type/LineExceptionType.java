package nextstep.subway.common.type;

public enum LineExceptionType {
    NOT_FOUND_LINE("존재하지 않는 라인이에요");

    private final String message;

    LineExceptionType(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
