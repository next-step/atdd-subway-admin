package nextstep.subway.common;

public enum ErrorMessage {
    NOT_FOUND("해당 id로 조회 결과가 없습니다."),
    ;

    private final String message;

    ErrorMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
