package nextstep.subway.common.exception;

/**
 * 데이터베이스 조회시 정보를 찾지 못할 때 throw 되는 예외입니다.
 */
public class NotFoundException extends DatabaseException {

    public NotFoundException() {
        super("Not Found Exception");
    }

    public NotFoundException(String message) {
        super(message);
    }

}
