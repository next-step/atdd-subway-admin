package nextstep.subway.dto;

public class ErrorResponse {

    private String errorMessage;

    private ErrorResponse(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public static ErrorResponse createErrorResponse(String message) {
        return new ErrorResponse(message);
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
