package nextstep.subway.line.domain;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Distance {

	@Column(nullable = false)
	private int distance;

	protected Distance() {}

	private Distance(int distance) {
		validatePositive(distance);
		this.distance = distance;
	}

	private void validatePositive(int distance) {
		if (distance <= 0) {
			throw new IllegalArgumentException("구간의 거리는 양수여야 합니다.");
		}
	}

	static Distance valueOf(int distance) {
		return new Distance(distance);
	}

	Distance minus(Distance otherSection) {
		return Distance.valueOf(this.distance - otherSection.distance);
	}

	Distance plus(Distance otherSection) {
		return Distance.valueOf(this.distance + otherSection.distance);
	}

	boolean isLessThan(Distance other) {
		return this.distance <= other.distance;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Distance other = (Distance)o;
		return distance == other.distance;
	}

	@Override
	public int hashCode() {
		return Objects.hash(distance);
	}

	@Override
	public String toString() {
		return "Distance{" + "distance=" + distance + '}';
	}
}
