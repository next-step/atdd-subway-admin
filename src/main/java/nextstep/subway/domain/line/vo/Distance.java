package nextstep.subway.domain.line.vo;

import java.util.Objects;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Embeddable;

@Embeddable
@Access(AccessType.FIELD)
public class Distance {
	private static final int MINIMUM_DISTANCE = 0;
	private static final String INVALID_DISTANCE_ERROR_MESSAGE = "거리는 0보다 커야 합니다.";
	private int value;

	public Distance(int value) {
		validate(value);
		this.value = value;
	}

	protected Distance() {
	}

	private static void validate(int value) {
		if (value <= MINIMUM_DISTANCE) {
			throw new IllegalArgumentException(INVALID_DISTANCE_ERROR_MESSAGE);
		}
	}

	public int getValue() {
		return value;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Distance distance1 = (Distance)o;
		return value == distance1.value;
	}

	@Override
	public int hashCode() {
		return Objects.hash(value);
	}
}

