package nextstep.subway.common.exception;

/**
 * 데이터베이스 데이터 생성, 업데이트 무결성 제약 조건을 위반 할 때 throw 되는 예외입니다.
 */
public class DuplicateException extends DatabaseException {

    public DuplicateException() {
        super("Not Found Exception");
    }

    public DuplicateException(String message) {
        super(message);
    }

}
