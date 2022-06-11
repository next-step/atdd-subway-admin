package nextstep.subway.dto;

public class ErrorResponse {

    private String errorMessage;

    public ErrorResponse(final String message) {
        this.errorMessage = message;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
