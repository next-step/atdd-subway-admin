package nextstep.subway.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Distance {
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
            throw new IllegalArgumentException("");
        }
        this.distance -= distance;
    }
}
