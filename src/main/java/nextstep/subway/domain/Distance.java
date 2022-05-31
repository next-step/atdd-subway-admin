package nextstep.subway.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import nextstep.subway.exception.SectionInvalidException;

@Embeddable
public class Distance {

    @Column
    private Integer distance;

    protected Distance() {
    }

    public Distance(Integer distance) {
        this.distance = distance;
    }

    public void minus(Distance distance) {
        if (this.distance <= distance.getDistance()) {
            throw new SectionInvalidException();
        }
        this.distance = this.distance - distance.getDistance();
    }

    public void plus(Distance distance) {
        this.distance = this.distance + distance.getDistance();
    }

    public Integer getDistance() {
        return distance;
    }
}
