package nextstep.subway.line.domain;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import nextstep.subway.common.exception.BadParameterException;

@Embeddable
public class Distance {
	public static final String EXCEPTION_MESSAGE_TOO_FAR_DISTANCE = "추가하려는 구간의 길이가 기존에 존재하는 길이와 같거나 깁니다.";

	@Column
	private int distance;

	protected Distance() {
	}

	public Distance(int distance) {
		this.distance = distance;
	}

	public void minus(Distance other) {
		if (this.distance <= other.distance) {
			throw new BadParameterException(EXCEPTION_MESSAGE_TOO_FAR_DISTANCE);
		}
		this.distance -= other.distance;
	}

	public int get() {
		return this.distance;
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
