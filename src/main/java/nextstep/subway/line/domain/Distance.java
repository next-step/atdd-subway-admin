package nextstep.subway.line.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class Distance {

	@Column(name = "distance", nullable = false)
	private final int distance;

	public Distance() {
		distance = 0;
	}

	public Distance(int distance) {
		if (distance < 0) {
			throw new IllegalArgumentException("distance cannot be low than zero");
		}
		this.distance = distance;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Distance distance1 = (Distance) o;
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

	public Distance minus(Distance secondOperand) {
		return new Distance(this.distance - secondOperand.distance);
	}

	public Distance plus(Distance secondOperand) {
		return new Distance(this.distance + secondOperand.distance);
	}
}
