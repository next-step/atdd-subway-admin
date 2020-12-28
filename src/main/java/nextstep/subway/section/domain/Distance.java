package nextstep.subway.section.domain;

import javax.persistence.Embeddable;

@Embeddable
public class Distance {
	private int distance;

	protected Distance() {
	}

	public Distance(int distance) {
		this.distance = distance;
	}

	public int get() {
		return distance;
	}

	public void subtract(int distance) {
		this.distance -= distance;
	}
}
