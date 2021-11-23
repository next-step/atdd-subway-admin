package nextstep.subway.line.exception;

import nextstep.subway.common.exception.ServiceException;
import nextstep.subway.line.domain.Line;

public class DuplicateLineNameException extends ServiceException {
    private final static String DEFAULT_MESSAGE = "중복된 노선명이 있습니다. : ";

    public DuplicateLineNameException(Line line) {
        super(DEFAULT_MESSAGE + line.getName());
    }
}
