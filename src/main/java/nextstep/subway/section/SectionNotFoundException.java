package nextstep.subway.section;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "section not found")
public class SectionNotFoundException extends RuntimeException {
}
