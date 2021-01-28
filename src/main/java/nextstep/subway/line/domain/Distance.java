package nextstep.subway.line.domain;

import javax.persistence.Embeddable;

@Embeddable
public class Distance {
	public static final String GREATER_THAN_ZERO = "거리는 0 보다 커야 합니다.";
	private final int distance;

	protected Distance() {
		distance = 0;
	}

	public Distance(int distance) {
		validate(distance);
		this.distance = distance;
	}

	private void validate(int distance) {
		if (distance <= 0) {
			throw new IllegalArgumentException(GREATER_THAN_ZERO);
		}
	}

	public Distance minus(Distance distance) {
		return new Distance(this.distance - distance.distance);
	}
}
