package nextstep.subway.section.domain;

import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class Distance {
    private static final int MIN_DISTANCE = 1;
    private int distance;

    protected Distance(){
        this.distance = MIN_DISTANCE;
    }

    public Distance(int distance) {
        if(MIN_DISTANCE > distance){
            throw new IllegalArgumentException(String.format("구간 거리는 %d 이상입니다.", MIN_DISTANCE));
        }
        this.distance = distance;
    }

    public boolean isShortDistance(Distance originDistance) {
        return originDistance.checkDistance(this.distance);
    }

    private boolean checkDistance(int newDistance) {
        return this.distance > newDistance;
    }

    public void adjustmentDistance(Distance newDistance) {
        this.distance = newDistance.getAdjustmentDistance(this.distance);
    }

    private int getAdjustmentDistance(int originDistance) {
        return originDistance - this.distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Distance distance1 = (Distance) o;
        return distance == distance1.distance;
    }

    @Override
    public int hashCode() {
        return Objects.hash(distance);
    }
}
