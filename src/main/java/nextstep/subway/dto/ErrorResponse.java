package nextstep.subway.dto;

import org.springframework.http.HttpStatus;

public class ErrorResponse {
    private int httpStatus;
    private String message;
    public ErrorResponse(int httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    public String getMessage() {
        return message;
    }
}
