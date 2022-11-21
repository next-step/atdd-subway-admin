package nextstep.subway.application.exception.type;

public enum AlreadyExceptionType {
    ALREADY_LINE_STATION("이미 존재 하는 LineStation 이에요");

    private final String message;

    AlreadyExceptionType(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
