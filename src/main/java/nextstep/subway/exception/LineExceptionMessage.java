package nextstep.subway.exception;

public enum LineExceptionMessage {
    NONE_EXISTS_LINE("라인이 존재하지 않습니다");
    String message;

    LineExceptionMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
