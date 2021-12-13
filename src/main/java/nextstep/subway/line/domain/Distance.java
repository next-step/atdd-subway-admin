package nextstep.subway.line.domain;

import javax.persistence.Embeddable;

@Embeddable
public class Distance {

    private static final int MINIUM_DISTANCE= 0;
    private static final String ERR_MSG_WORNG_DISTANCE= "두 역 사이의 거리가 0보다 같거나 작을수 없습니다.";

    private int distance;

    public Distance() {
    }

    public Distance(int distance) {
        this.distance = distance;
    }

    public static Distance of(int distance) {
        return new Distance(distance);
    }

    public int getDistance() {
        return distance;
    }

    public boolean isGreaterThan(Distance distance) {
        return this.distance > distance.getDistance();
    }

    public int getDistanceBetweenTwoDistances(Distance distance) {
        int gap = this.distance - distance.getDistance();
        if(gap <= MINIUM_DISTANCE) {
            throw new IllegalStateException(ERR_MSG_WORNG_DISTANCE);
        }
        return gap;
    }

    public void addDistance(Distance distance) {
        this.distance += distance.getDistance();
    }
}

