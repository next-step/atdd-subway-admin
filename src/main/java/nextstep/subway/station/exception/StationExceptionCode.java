package nextstep.subway.station.exception;

public enum StationExceptionCode {

    REQUIRED_NAME("The station name is a required field."),
    NOT_FOUND_BY_ID("The station not found by id.");

    private String title = "[ERROR] ";
    private String message;

    StationExceptionCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return title + message;
    }
}
