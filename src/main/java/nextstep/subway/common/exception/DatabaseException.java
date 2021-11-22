package nextstep.subway.common.exception;

/**
 * application Layer 에서 JpaRepository 사용할 때 발생 하는 예외입니다. `DatabaseException` 상속된 예외들은 ui layer
 * 에서`RestResponseEntityExceptionHandler` 클래스의 `@ExceptionHandler` 에 의해 핸들링 됩니다.
 */
public class DatabaseException extends RuntimeException {

    public DatabaseException() {
    }

    public DatabaseException(String message) {
        super(message);
    }

    public DatabaseException(String message, Throwable cause, boolean enableSuppression,
        boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public DatabaseException(Throwable cause) {
        super(cause);
    }
}
