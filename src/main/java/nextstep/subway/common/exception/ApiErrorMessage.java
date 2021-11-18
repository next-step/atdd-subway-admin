package nextstep.subway.common.exception;

public class ApiErrorMessage {

    private String message;

    public ApiErrorMessage() {
    }

    public ApiErrorMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
