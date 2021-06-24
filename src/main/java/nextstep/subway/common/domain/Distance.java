package nextstep.subway.common.domain;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Distance {

	private static final int ZERO = 0;
	private static final String COLUMN_DESCRIPTION = "역 사이 간격";

	@Column
	private int distance;

	protected Distance() {
	}

	private Distance(int distance) {
		validateOverZero(distance);
		this.distance = distance;
	}

	public static Distance generate(int distance) {
		return new Distance(distance);
	}

	private void validateOverZero(int distance) {
		if (distance <= ZERO) {
			throw new IllegalArgumentException(COLUMN_DESCRIPTION + "은 0을 초과하는 숫자여야 합니다.");
		}
	}

	public void adjust(Distance distance) {
		int adjustDistance = this.distance - distance.distance;
		validateOverZero(adjustDistance);
		this.distance = adjustDistance;
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
