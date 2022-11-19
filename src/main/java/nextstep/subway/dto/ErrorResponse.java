package nextstep.subway.dto;

import nextstep.subway.exception.BadRequestException;

public class ErrorResponse {

    private String message;

    private ErrorResponse() {
    }

    public ErrorResponse(BadRequestException exception) {
        this.message = exception.getMessage();
    }

    public String getMessage() {
        return message;
    }
}
