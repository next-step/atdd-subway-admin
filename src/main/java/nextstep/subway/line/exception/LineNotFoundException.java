package nextstep.subway.line.exception;

import javax.persistence.EntityNotFoundException;

import static java.lang.String.format;

public class LineNotFoundException extends EntityNotFoundException {
    public LineNotFoundException(Long id) {
        super(format("id가 %d인 노선이 존재 하지 않습니다.", id));
    }
}
