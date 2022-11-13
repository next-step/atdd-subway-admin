package nextstep.subway.dto;

import nextstep.subway.exception.CannotAddSectionException;

public class ErrorResponse {

    private String message;

    private ErrorResponse() {
    }

    public ErrorResponse(CannotAddSectionException exception) {
        this.message = exception.getMessage();
    }

    public String getMessage() {
        return message;
    }
}
