package nextstep.subway.common.exception;

public class InvalidEntityRequiredException extends RuntimeException {
    private static final String INVALID_ENTITY_REQUIRED_MESSAGE = "필수로 필요한 Entity member가 들어오지 않았습니다. : ";
    private static final long serialVersionUID = 4L;

    public InvalidEntityRequiredException(String name) {
        super(INVALID_ENTITY_REQUIRED_MESSAGE + name);
    }
}
