package nextstep.subway.section.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Distance {

    @Column(nullable = false)
    private Long distance;

    protected Distance() {
    }

    public Distance(Long distance) {
        initDistance(distance);
    }

    public Long compareTo(Distance distanceObj) {
        return this.distance - distanceObj.distance;
    }

    public void subtract(Distance distanceObj) {
        long subtractDistance = this.distance - distanceObj.distance;
        initDistance(subtractDistance);
    }

    private void initDistance(Long distance) {
        if (distance <= 0) {
            throw new IllegalArgumentException("구간 거리는 0보다 커야합니다.");
        }

        this.distance = distance;
    }

}
