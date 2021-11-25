package nextstep.subway.exceptions;

public class DuplicateLineException extends RuntimeException {
  public DuplicateLineException() {
    super();
  }

  public DuplicateLineException(String message) {
    super(message);
  }
}
