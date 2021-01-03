package nextstep.subway.section.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Distance {
    @Column(nullable = false)
    private int distance;

    public Distance(int distance) {
        this.distance = distance;
    }

    public Distance add(Distance otherDistance) {
        return new Distance(this.distance + otherDistance.getDistance());
    }

    public Distance minus(Distance otherDistance) {
        return new Distance(this.distance - otherDistance.getDistance());
    }

    public boolean isEqualOrMore(Distance otherDistance) {
        return this.distance >= otherDistance.getDistance();
    }
}
