package nextstep.subway.exception;

public enum LineExceptionMessage {
    NONE_EXISTS_LINE("라인이 존재하지 않습니다"),
    EMPTY_NAME("노선이름을 입력해야합니다"),
    EMPTY_STATION("역정보를 입력해야합니다");

    String message;

    LineExceptionMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
