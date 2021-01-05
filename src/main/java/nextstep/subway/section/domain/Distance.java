package nextstep.subway.section.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.section.exception.InvalidAddSectionException;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Distance {
    @Column(nullable = false)
    private int distance;

    public Distance(int distance) {
        this.distance = distance;
    }

    public Distance add(Distance otherDistance) {
        return new Distance(this.distance + otherDistance.getDistance());
    }

    public Distance minus(Distance otherDistance) {
        checkDistance(otherDistance);
        return new Distance(this.distance - otherDistance.getDistance());
    }

    private void checkDistance(Distance otherDistance) {
        if (isLessOrEqualDistance(otherDistance)) {
            throw new InvalidAddSectionException("기존 역 사이 길이보다 크거나 같으면 등록을 할 수가 없습니다.");
        }
    }

    private boolean isLessOrEqualDistance(Distance otherDistance) {
        return this.distance <= otherDistance.getDistance();
    }

    public int getDistance() {
        return distance;
    }
}
