package nextstep.subway.application.exception.type;

public enum StationExceptionType {
    NOT_FOUND_STATION("존재하지 않는 역이에요");

    private final String message;

    StationExceptionType(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
