package nextstep.subway.common;

public class ErrorMessageResponse {

    private String errorMessage;

    public ErrorMessageResponse() {
    }

    private ErrorMessageResponse(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public static ErrorMessageResponse of(String errorMessage) {
        return new ErrorMessageResponse(errorMessage);
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
