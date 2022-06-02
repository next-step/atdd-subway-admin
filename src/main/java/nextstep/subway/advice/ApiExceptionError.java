package nextstep.subway.advice;

public class ApiExceptionError {

    private String message;

    public ApiExceptionError() {
    }

    public ApiExceptionError(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
