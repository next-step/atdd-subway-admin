package nextstep.subway.section.domain;

import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class Distance {
    private static final int MIN_DISTANCE = 1;
    private final int distance;

    protected Distance(){
        this.distance = MIN_DISTANCE;
    }

    public Distance(int distance) {
        if(MIN_DISTANCE > distance){
            throw new IllegalArgumentException(String.format("구간 거리는 %d 이상입니다.", MIN_DISTANCE));
        }
        this.distance = distance;
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
