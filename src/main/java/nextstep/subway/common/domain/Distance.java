package nextstep.subway.common.domain;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Distance {

	private static final double ZERO = 0.0D;
	private static final String COLUMN_DESCRIPTION = "역 사이 간격";

	@Column
	private double distance;

	protected Distance() {
	}

	private Distance(double distance) {
		validateDistance(distance);
		this.distance = distance;
	}

	public static Distance generate(double distance) {
		return new Distance(distance);
	}

	private void validateDistance(double distance) {
		validateOverZero(distance);
	}

	private void validateOverZero(double distance) {
		if (distance <= ZERO) {
			throw new IllegalArgumentException(COLUMN_DESCRIPTION + "은 0을 초과하는 숫자여야 합니다.");
		}
	}

	public double distance() {
		return distance;
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}
		if (!(object instanceof Distance)) {
			return false;
		}
		Distance distance1 = (Distance)object;
		return Double.compare(distance1.distance, distance) == 0;
	}

	@Override
	public int hashCode() {
		return Objects.hash(distance);
	}
}
