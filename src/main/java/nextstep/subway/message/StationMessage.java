package nextstep.subway.message;

public enum StationMessage {
    ERROR_STATION_NAME_SHOULD_BE_NOT_NULL("역 이름은 필수입니다."),
    ERROR_NOT_FOUND_STATION_BY_ID("주어진 ID와 일치하는 지하철 역을 찾을 수 없습니다");

    private final String message;

    StationMessage(String message) {
        this.message = message;
    }

    public String message() {
        return message;
    }
}
