package nextstep.subway.line.exception;

import nextstep.subway.common.exception.NotFoundException;

public class LineNotFoundException extends NotFoundException {
    private final static String DEFAULT_MESSAGE = "아이디 %d는 없는 노선입니다.";

    public LineNotFoundException(Long id) {
        super(String.format(DEFAULT_MESSAGE, id));
    }
}
