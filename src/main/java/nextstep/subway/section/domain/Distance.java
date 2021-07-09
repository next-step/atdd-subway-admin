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

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

	public int subtract(int distance) {
		if(this.distance <= distance){
			throw new IllegalArgumentException("길이는 기존 역 사이의 길이보다 작아야합니다.");
		}
		return this.distance - distance;
	}

	public int plus(int distance){
		return this.distance + distance;
	}
}
