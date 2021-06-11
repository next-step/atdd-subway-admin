package nextstep.subway.utils;

import nextstep.subway.section.domain.Section;

public class DistanceCalculator {
    private static final int MINIMUM_DISTANCE = 1;
    private int distance;

    public DistanceCalculator() {
        this.distance = 0;
    }

    public int getDistance() {
        return distance;
    }

    public DistanceCalculator addDistance(int distance) {
        this.distance += distance;
        if (this.distance < MINIMUM_DISTANCE) {
            throw new IllegalArgumentException("합산된 구간의 길이가 1보다 작습니다.");
        }
        return this;
    }

    public boolean isGreaterThan(int distance) {
        return this.distance > distance;
    }

    public boolean isEqualTo(int distance) {
        return this.distance == distance;
    }

    public boolean isGreaterThanOrEqualTo(int distance) {
        return this.distance >= distance;
    }

    public int calculateDistance(Section appendSection, Section baseSection) {
        return appendSection.getDistance() - (this.distance - baseSection.getDistance());
    }

    public int calculateDistance(Section appendSection) {
        if (this.isGreaterThan(appendSection.getDistance())) {
            return this.distance - appendSection.getDistance();
        }
        return appendSection.getDistance() - this.distance;
    }
}
