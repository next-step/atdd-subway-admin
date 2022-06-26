package nextstep.subway.error.exception;

import nextstep.subway.error.SubwayError;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class SectionInvalidException extends SubWayApiException {
    public SectionInvalidException(String message) {
        super(SubwayError.SECTION_INVALID_REQUEST, message);
    }
}
