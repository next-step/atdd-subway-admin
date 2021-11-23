package nextstep.subway.common.exception;

public class NotFoundEntityException extends RuntimeException {
    private static final String NOT_FOUND_ENTITY_MESSAGE = "Entity를 찾을 수 없습니다. : ";
    private static final long serialVersionUID = 1L;

    public NotFoundEntityException(Long id) {
        super(NOT_FOUND_ENTITY_MESSAGE + id);
    }
}
