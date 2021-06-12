package nextstep.subway.section.domain;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import nextstep.subway.exception.SubwayLogicException;

@Embeddable
public class Distance {

	private static final String DISTANCE_INVALID_MESSAGE = "distance는 0이상 이어야 합니다.";

	@Column(name = "distance")
	private int distance;

	protected Distance() {
	}

	public Distance(int distance) {
		if (distance <= 0) {
			throw new SubwayLogicException(DISTANCE_INVALID_MESSAGE);
		}
		this.distance = distance;
	}

	public int getDistance() {
		return distance;
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

	@Override
	public String toString() {
		return "Distance{" +
			"distance=" + distance +
			'}';
	}

	public boolean isShorter(Distance distance) {
		return this.distance < distance.getDistance();
	}

	public Distance minus(Distance distance) {
		return new Distance(this.distance - distance.getDistance());
	}
}
