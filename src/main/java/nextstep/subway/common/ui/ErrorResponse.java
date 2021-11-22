package nextstep.subway.common.ui;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.BindingResult;

import java.util.stream.Collectors;

public class ErrorResponse {
    private final String message;

    public ErrorResponse(String message) {
        this.message = message;
    }

    public ErrorResponse(Exception error) {
        this(error.getMessage());
    }

    public ErrorResponse(BindingResult bindingResult) {
       this.message = bindingResult.getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(","));
    }

    public String getMessage() {
        return message;
    }
}
