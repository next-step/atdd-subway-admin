package nextstep.subway.dto;

import nextstep.subway.application.Distance;

public class DistanceResponse {

    private int distance;

    public DistanceResponse(int distance) {
        this.distance = distance;
    }

    public static DistanceResponse of(Distance distance) {
        return new DistanceResponse(distance.getDistance());
    }

    public int getDistance() {
        return distance;
    }
}
