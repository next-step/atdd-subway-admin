package nextstep.subway.common;

import org.springframework.http.HttpStatus;

public class ErrorResponse {
  private final HttpStatus httpStatus;
  private final String message;

  public ErrorResponse(HttpStatus httpStatus, String message) {
    this.httpStatus = httpStatus;
    this.message = message;
  }

  public static ErrorResponse of(HttpStatus httpStatus, String message) {
    return new ErrorResponse(httpStatus, message);
  }

  public HttpStatus getHttpStatus() {
    return httpStatus;
  }

  public String getMessage() {
    return message;
  }

}
