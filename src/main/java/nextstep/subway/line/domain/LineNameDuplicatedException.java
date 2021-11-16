package nextstep.subway.line.domain;

import nextstep.subway.global.BusinessException;

public class LineNameDuplicatedException extends BusinessException {

    public static final String MESSAGE = "이름이 동일한 노선이 존재합니다.";

    public LineNameDuplicatedException() {
        super(MESSAGE);
    }
}
