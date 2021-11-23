package nextstep.subway.line.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Distance {

	@Column(name = "distance")
	private int distance;

	protected Distance() {
	}

	public Distance(int distance) {
		this.distance = distance;
	}

	public int getDistance() {
		return distance;
	}

	public void divideDistance(Distance distance) {
		this.distance -= distance.getDistance();
	}
}
