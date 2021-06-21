package nextstep.subway.section.domain;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import nextstep.subway.section.exception.InvalidDistanceException;

@Embeddable
public class Distance {

	private static final int MIN_DISTANCE = 0;

	@Column
	private int distance;

	protected Distance() {

	}

	public Distance(int distance) {
		validate(distance);
		this.distance = distance;
	}

	private void validate(int distance) {
		if (MIN_DISTANCE >= distance) {
			throw new InvalidDistanceException("거리는 0보다 작을 수 없습니다.");
		}
	}

	public Distance getPlusDistance(Distance distance) {
		return new Distance(this.distance + distance.distance);
	}

	public Distance getDifferenceDistance(Distance distance) {
		int differenceDistance = this.distance - distance.distance;
		validateDifferenceDistance(differenceDistance);
		return new Distance(differenceDistance);
	}

	private void validateDifferenceDistance(int differenceDistance) {
		if (differenceDistance < 0) {
			throw new InvalidDistanceException("새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록할 수 없습니다.");
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Distance distance1 = (Distance)o;
		return distance == distance1.distance;
	}

	@Override
	public int hashCode() {
		return Objects.hash(distance);
	}
}
