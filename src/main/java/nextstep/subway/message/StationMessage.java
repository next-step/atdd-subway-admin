package nextstep.subway.message;

public enum StationMessage {
    ERROR_STATION_NAME_SHOULD_BE_NOT_NULL("역 이름은 필수입니다.");

    private final String message;

    StationMessage(String message) {
        this.message = message;
    }

    public String message() {
        return message;
    }
}
