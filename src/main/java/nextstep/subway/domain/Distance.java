package nextstep.subway.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Distance {
    @Column(name = "distance")
    private int value;

    public Distance(final int value) {
        this.value = value;
    }

    protected Distance() {

    }

    public void change(final int distance) {
        this.value = distance;
    }

    public boolean isEqualToOrLessThan(final int distance) {
        return this.value <= distance;
    }

    public int getValue() {
        return value;
    }

    public void minus(final int distance) {
        this.value -= distance;
    }
}
