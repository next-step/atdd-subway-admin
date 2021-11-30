package nextstep.subway.line.domain;

import javax.persistence.Embeddable;

@Embeddable
public class Distance {
    
    private int distance;
    
    protected Distance() {
    }


    private Distance(int distance) {
        validateDistance(distance);
        this.distance = distance;
    }
    
    public static Distance from(int distance) {
        return new Distance(distance);
    }

    public int getDistance() {
        return distance;
    }
    
    private void validateDistance(int distance) {
        if (distance <= 0) {
            throw new IllegalArgumentException("구간의 길이는 0보다 커야합니다.");
        }
    }
    
    private void checkShorter(Distance distance) {
        if (this.distance <= distance.getDistance()) {
            throw new IllegalArgumentException(String.format("길이가 맞지 않는 노선입니다.(%d)", this.distance));
        }
    }
    
    public Distance move(Distance distance) {
        checkShorter(distance);
        return Distance.from(this.distance - distance.getDistance());
    }

}
