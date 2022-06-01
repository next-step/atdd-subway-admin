package nextstep.subway.common.exception;

public enum ErrorMessage {
    LINE_NOT_FOUND("존재하지 않는 노선입니다.");

    private final String message;

    ErrorMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
