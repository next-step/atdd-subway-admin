package nextstep.subway.dto;

public class ErrorResponse {
    private String message;

    public String getMessage() {
        return message;
    }

    public ErrorResponse() {
    }

    public ErrorResponse(String message) {
        this.message = message;
    }
}
