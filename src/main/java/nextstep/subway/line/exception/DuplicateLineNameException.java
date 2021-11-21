package nextstep.subway.line.exception;

import nextstep.subway.line.domain.Line;

public class DuplicateLineNameException extends RuntimeException {
    private final static String DEFAULT_MESSAGE = "중복된 노선명이 있습니다. : ";

    public DuplicateLineNameException(Line line) {
        super(DEFAULT_MESSAGE + line.getName());
    }
}
