package nextstep.subway.section.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Distance {

    @Column(nullable = false)
    private int distance;

    protected Distance() { }

    private Distance(int distance) {
        set(distance);
    }

    public static Distance valueOf(int distance) {
        return new Distance(distance);
    }

    private void validateValue(int distance) {
        if (distance < 1 || distance >= Integer.MAX_VALUE) {
            throw new IllegalArgumentException("유효하지 않은 Section 의 역들 사이거리 입니다.");
        }
    }

    public int get() {
        return distance;
    }

    public void set(int distance) {
        validateValue(distance);
        this.distance = distance;
    }

    public void shorten(int distance) {
        set(this.distance - distance);
    }
}
