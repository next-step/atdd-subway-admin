package nextstep.subway.line.exception;

public enum SectionExceptionCode {

    REQUIRED_LINE("The line is a required field."),
    REQUIRED_UP_STATION("The upStation is a required field."),
    REQUIRED_DOWN_STATION("The downStation is a required field."),
    CANNOT_BE_THE_SAME_EACH_STATION("The upStation and downStation cannot be same."),
    INVALID_DISTANCE("The distance does not allow negative number.");

    private String title = "[ERROR] ";
    private String message;

    SectionExceptionCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return title + message;
    }

}
