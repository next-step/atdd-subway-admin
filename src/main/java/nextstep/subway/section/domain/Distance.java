package nextstep.subway.section.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Distance {
    public static final String DISTANCE_MINUS_ERROR_MSG = "Minus 하려는 길이가 더 길어서, 해당 길이를 Minus 할 수 없습니다.";

    @Column
    private Integer distance;

    public Distance() {
    }

    public Distance(Integer distance) {
        this.distance = distance;
    }

    public Integer getDistance() {
        return distance;
    }

    public void minus(Integer distance) {
        if(this.distance - distance <= 0) {
            throw new IllegalArgumentException(DISTANCE_MINUS_ERROR_MSG);
        }
        this.distance -= distance;
    }
}
