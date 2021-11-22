package nextstep.subway.common.exception;

/**
 * 데이터베이스 생성,업데이트시 유니크 값 중복 일때 발생하는 throw 예외입니다.
 */
public class DuplicateException extends DatabaseException {

    public DuplicateException() {
        super("Duplicate Exception");
    }

    public DuplicateException(String message) {
        super(message);
    }

}
