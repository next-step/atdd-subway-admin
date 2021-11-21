package nextstep.subway.common.exception;

import org.springframework.dao.DataIntegrityViolationException;

public class DuplicateEntityException extends DataIntegrityViolationException {
    private static final long serialVersionUID = 2L;

    public DuplicateEntityException(String message) {
        super(message);
    }
}
