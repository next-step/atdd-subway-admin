package nextstep.subway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestControllerExceptionAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(SubwayRuntimeException.class)
    public ResponseEntity<Object> handle(final SubwayRuntimeException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse(e));
    }

    public static class ErrorResponse {

        private final Error error;

        private ErrorResponse() {
            this.error = new Error();
        }

        public ErrorResponse(final Throwable cause) {
            this.error = new Error(cause.getMessage());
        }

        public Error getError() {
            return error;
        }

        public static class Error {
            private final String message;

            private Error() {
                this.message = "";
            }

            public Error(String message) {
                this.message = message;
            }

            public String getMessage() {
                return message;
            }
        }
    }
}
