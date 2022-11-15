package nextstep.subway.domain;

import javax.persistence.Embeddable;

@Embeddable
public class Distance {

    private int distance;

    protected Distance() {
    }

    public Distance(int distance) {
        valiPositives(distance);
        this.distance = distance;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    private void valiPositives(int distance) {
        if (distance <= 0) {
            throw new IllegalArgumentException("distance는 0보다 커야합니다. 입력값:" + distance);
        }
    }
}
