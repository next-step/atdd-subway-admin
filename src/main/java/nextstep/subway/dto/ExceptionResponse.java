package nextstep.subway.dto;

public class ExceptionResponse {
    private String errorMessage;
    public ExceptionResponse() {
    }

    public ExceptionResponse(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public static ExceptionResponse from(IllegalArgumentException ex) {
        return new ExceptionResponse(ex.getMessage());
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
