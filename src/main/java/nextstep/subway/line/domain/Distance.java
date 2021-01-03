package nextstep.subway.line.domain;

public class Distance {
    Long distance;

    public Distance(Long distance) {
        this.distance = distance;
    }

    public Long get() {
        return distance;
    }

    public Long addDistanceToLong(Distance addDistance) {
        return distance + addDistance.get();
    }
}
