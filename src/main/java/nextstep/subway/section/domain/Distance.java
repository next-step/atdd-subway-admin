package nextstep.subway.section.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Distance {

	@Column
	private int distance;

	protected Distance() {
		
	}

	public Distance(int distance) {
		this.distance = distance;
	}

	public int getDistance() {
		return this.distance;
	}
}
