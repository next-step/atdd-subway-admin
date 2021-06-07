package nextstep.subway.common;

public class ErrorMessageResponse {

    private String errorMessage;

    public ErrorMessageResponse() {
    }

    public ErrorMessageResponse(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
