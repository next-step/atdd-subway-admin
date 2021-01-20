package nextstep.subway.line.domain;

import java.util.Objects;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Embeddable;

@Access(AccessType.FIELD)
@Embeddable
public class Distance {

	private Integer distance;

	protected Distance() {
	}

	public Distance(final Integer distance) {
		validate(distance);
		this.distance = distance;
	}

	private void validate(final Integer distance) {
		if (distance != null && distance <= 0) {
			throw new IllegalArgumentException("구간 길이는 1 이상의 정수여야 합니다.");
		}
	}

	public int getDistance() {
		return distance;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		final Distance distance1 = (Distance)o;
		return distance == distance1.distance;
	}

	@Override
	public int hashCode() {
		return Objects.hash(distance);
	}

	@Override
	public String toString() {
		return String.valueOf(this.distance);
	}

	public Distance minus(final Distance distance) {
		return new Distance(this.distance - distance.getDistance());
	}
}
