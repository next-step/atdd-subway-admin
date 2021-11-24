package nextstep.subway.common.exception;

public class DuplicateEntityException extends RuntimeException {
    private static final String DUPLICATE_ENTITY_MESSAGE = "이미 존재하는 Entity가 있습니다.";
    private static final long serialVersionUID = 2L;

    public DuplicateEntityException() {
        super(DUPLICATE_ENTITY_MESSAGE);
    }
}
