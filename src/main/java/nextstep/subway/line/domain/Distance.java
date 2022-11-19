package nextstep.subway.line.domain;

import javax.persistence.Embeddable;

@Embeddable
public class Distance {

    public static final int MINIMUM_DISTANCE = 0;
    private int distance;

    protected Distance() {
    }

    public Distance(int distance) {
        validate(distance);
        this.distance = distance;
    }

    private static void validate(int distance) {
        if (distance <= MINIMUM_DISTANCE) {
            throw new IllegalArgumentException();
        }
    }
}
