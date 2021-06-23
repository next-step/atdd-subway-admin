package nextstep.subway.line.exception;

import static java.lang.String.format;

public class LineNotFoundException extends RuntimeException{
    public LineNotFoundException(Long id) {
        super(format("id가 %d인 노선이 존재 하지 않습니다.", id));
    }
}
