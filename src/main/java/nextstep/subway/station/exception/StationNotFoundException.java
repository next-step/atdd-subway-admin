package nextstep.subway.station.exception;

import javax.persistence.EntityNotFoundException;

import static java.lang.String.format;

public class StationNotFoundException extends EntityNotFoundException {
    public StationNotFoundException(Long id) {
        super(format("id가 %d인 역이 존재 하지 않습니다.", id));
    }
}
