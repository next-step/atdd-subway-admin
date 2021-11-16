package nextstep.subway.exception;

public class NotFoundDataException extends RuntimeException{
    private static final String NOT_FOUND_DATA_MESSAGE = "데이터를 찾을 수 없습니다.";

    public NotFoundDataException(){
        super(NOT_FOUND_DATA_MESSAGE);
    }
}
