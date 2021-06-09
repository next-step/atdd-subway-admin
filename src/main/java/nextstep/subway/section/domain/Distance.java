package nextstep.subway.section.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Distance {

	private static final int MIN_DISTANCE = 0;

	@Column
	private int distance;

	protected Distance() {

	}

	public Distance(int distance) {
		validate(distance);
		this.distance = distance;
	}

	public int getDistance() {
		return this.distance;
	}

	private void validate(int distance) {
		if(MIN_DISTANCE >= distance)
			throw new IllegalArgumentException("거리는 0보다 작을 수 없습니다.");
	}
}
