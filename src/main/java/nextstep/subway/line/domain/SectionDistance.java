package nextstep.subway.line.domain;

import javax.persistence.Embeddable;

@Embeddable
public class SectionDistance {
    private Integer distance;

    public SectionDistance() {
    }

    public SectionDistance(Integer distance) {
        this.distance = distance;
    }

    public SectionDistance minus(SectionDistance requestDistance) {
        return new SectionDistance(this.distance - requestDistance.distance);
    }

    public Integer getDistance() {
        return distance;
    }

    public void setDistance(Integer distance) {
        this.distance = distance;
    }

    public void checkDistanceWith(SectionDistance newDistance) {
        if (distance <= newDistance.distance) {
            throw new RuntimeException("Invalid Distance");
        }
    }
}
