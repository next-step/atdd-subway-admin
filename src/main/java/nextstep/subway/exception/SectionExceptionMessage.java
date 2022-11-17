package nextstep.subway.exception;

public enum SectionExceptionMessage {
    LESS_THEN_ZERO_DISTANCE("거리값은 양수만 허용합니다");

    String message;

    SectionExceptionMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
