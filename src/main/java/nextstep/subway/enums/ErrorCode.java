package nextstep.subway.enums;

public enum ErrorCode {
    DEFAULT("일시적인 오류가 발생했습니다."),
    STATION_NAME_ALREADY_EXISTS("이미 같은 이름의 지하철역이 있습니다."),
    LINE_NAME_ALREADY_EXISTS("이미 같은 이름의 지하철역이 있습니다.");

    private String message;

    ErrorCode(final String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}
