package nextstep.subway.section.domain;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Distance {
	@Column
	private int distance;

	protected Distance() {
	}

	private Distance(int distance) {
		validate(distance);
		this.distance = distance;
	}

	public static Distance from(int distance) {
		return new Distance(distance);
	}

	private void validate(int distance) {
		if (distance > 0) {
			return;
		}
		throw new IllegalArgumentException("거리는 0보다 커야 합니다.");
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
