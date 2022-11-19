package nextstep.subway.line.exception;

import java.util.NoSuchElementException;

public class NoStationException extends NoSuchElementException {

    public NoStationException() {
        super("지하철역 정보가 없습니다.");
    }
}
