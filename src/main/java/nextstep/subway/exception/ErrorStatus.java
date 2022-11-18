package nextstep.subway.exception;

public enum ErrorStatus {
    BAD_REQUEST_NAME("[name] is not null or empty"),
    BAD_REQUEST_COLOR("[color] is not null or empty"),
    BAD_REQUEST_DISTANCE("[distance] must over than 0"),
    BAD_REQUEST_STATION_ID("[station id] must over than 1"),
    DUPLICATE_SECTION("[section] upStaion and downStaion do not duplicate"),
    DISTANCE_LENGTH("[distance] new Section distance must small exist section distance");
    private String message;

    ErrorStatus(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
