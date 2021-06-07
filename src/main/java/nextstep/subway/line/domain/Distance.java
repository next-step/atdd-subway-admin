package nextstep.subway.line.domain;

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

	public static Distance valueOf(int distance) {
		return new Distance(distance);
	}
}
