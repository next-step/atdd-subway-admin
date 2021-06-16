package nextstep.subway.section.domain;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

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

	public int getDistance() {
		return this.distance;
	}

	private void validate(int distance) {
		if(MIN_DISTANCE >= distance)
			throw new IllegalArgumentException("거리는 0보다 작을 수 없습니다.");
	}

	public boolean isMoreThan(Distance otherDistance) {
		return this.distance < otherDistance.distance;
	}

	public Distance getDifferenceDistance(Distance distance) {
		int differenceDistance = this.distance - distance.distance;
		if(differenceDistance < 0) {
			differenceDistance = differenceDistance * -1;
		}
		return new Distance(differenceDistance);
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
