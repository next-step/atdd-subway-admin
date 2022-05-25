package nextstep.subway.line.domain;

import nextstep.subway.core.domain.NotFoundException;

public class NotFoundLineException extends NotFoundException {

    public NotFoundLineException(Long id) {
        super(String.format("해당하는 지하철 노선을 찾을 수 없습니다. (id: %d)", id));
    }
}
