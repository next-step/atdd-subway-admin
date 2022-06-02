package nextstep.subway.section.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Distance {
    public static final String SECTION_DISTANCE_MINUS_ERROR_MSG = "중간에 포함되는 구간이 길이가 감싸고 있는 구간보다 작아야합니다.";

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
            throw new IllegalArgumentException(SECTION_DISTANCE_MINUS_ERROR_MSG);
        }
        this.distance -= distance;
    }
}
