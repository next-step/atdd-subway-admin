package nextstep.subway.exception;

public class NotFoundException extends RuntimeException{

    private static final String NOT_FOUND_EXCEPTION_MESSAGE = "찾으시는 데이터가 없습니다.";

    public NotFoundException() {
        super(NOT_FOUND_EXCEPTION_MESSAGE);
    }

    public NotFoundException(String message) {
        super(message);
    }
}
