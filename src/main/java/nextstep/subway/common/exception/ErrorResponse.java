package nextstep.subway.common.exception;

public class ErrorResponse {
    private final String errorMessage;

    protected ErrorResponse(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
