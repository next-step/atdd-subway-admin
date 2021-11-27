package nextstep.subway.domain.section.domain;

import nextstep.subway.domain.section.exception.DistanceExcessException;
import nextstep.subway.domain.section.exception.DistanceUnderException;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Distance {

    private static final int MINIMUM_DISTANCE = 1;

    @Column(name = "distance")
    private int distance;

    protected Distance() {
    }

    public Distance(final int distance) {
        distanceValidation(distance);
        this.distance = distance;
    }

    private void distanceValidation(int distance) {
        if (distance < MINIMUM_DISTANCE) {
            throw new DistanceUnderException();
        }
    }

    public Distance minus(final Distance newSectionDistance) {
        distanceValidation(newSectionDistance.getDistance());

        final int oldSectionDistance = this.distance - newSectionDistance.getDistance();

        if (oldSectionDistance <= 0) {
            throw new DistanceExcessException();
        }
        return new Distance(oldSectionDistance);
    }

    public int getDistance() {
        return distance;
    }

    public Distance plus(final Distance distance) {
        distanceValidation(distance.getDistance());

        final int oldSectionDistance = this.distance + distance.getDistance();

        if (oldSectionDistance <= 0) {
            throw new DistanceExcessException();
        }
        return new Distance(oldSectionDistance);
    }
}