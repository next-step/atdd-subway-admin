package nextstep.subway.line.domain;

import static nextstep.subway.common.ErrorCode.*;

import java.io.Serializable;

import javax.persistence.Embeddable;

import nextstep.subway.common.ServiceException;

@Embeddable
public class Distance implements Serializable {

    private int distance = 0;

    public Distance(int distance) {
        if (distance < 0) {
            throw new ServiceException(SERVER_ERROR, "구간의 거리가 0 또는 0보다 작을 수 없습니다.");
        }
        this.distance = distance;
    }

    protected Distance() {
    }
}
