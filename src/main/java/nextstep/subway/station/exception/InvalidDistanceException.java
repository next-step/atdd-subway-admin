package nextstep.subway.station.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidDistanceException extends RuntimeException {

    public InvalidDistanceException() {
        super("구간의 길이가 기존에 존재하는 길이와 같거나 깁니다.");
    }
}
