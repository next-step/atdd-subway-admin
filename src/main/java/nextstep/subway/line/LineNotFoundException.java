package nextstep.subway.line;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "line not found")
public class LineNotFoundException extends RuntimeException {
}
