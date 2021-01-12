package nextstep.subway.section.domain;

import javax.persistence.Embeddable;

@Embeddable
public class Distance {
	private static final int MINIMUM_DISTANCE = 1;

	private int distance;

	protected Distance() {
	}

	public Distance(int distance) {
		validateMinimumDistance(distance);
		this.distance = distance;
	}

	public Distance(String distance) {
		this(Integer.parseInt(distance));
	}

	public int getDistance() {
		return distance;
	}

	public int addDistance(Distance distance) {
		return this.distance + distance.getDistance();
	}

	public int subtractDistance(Distance distance) {
		return this.distance - distance.getDistance();
	}

	private void validateMinimumDistance(int distance) {
		if (MINIMUM_DISTANCE > distance) {
			throw new IllegalArgumentException("거리는 1 이상만 가능합니다.");
		}
	}
}
