package nextstep.subway.line.exception;

import nextstep.subway.exception.ServiceException;
import org.springframework.http.HttpStatus;

public class StationNotDeleteException extends ServiceException {

    private static final String message = "지하철 구간은 최소 한개 이상 존재 해야합니다.";

    public StationNotDeleteException() {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
