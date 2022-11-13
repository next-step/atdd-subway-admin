package nextstep.subway.exception;

import java.util.NoSuchElementException;

public class NoLineException extends NoSuchElementException {

    public NoLineException() {
        super("노선 정보가 없습니다.");
    }
}
