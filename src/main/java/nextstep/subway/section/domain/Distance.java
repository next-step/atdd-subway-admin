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
		// distance == 0은 상행종점만 갖는 값으로 거리 계산을 다시 하지않음
		if (this.distance == 0) {
			return ;
		}
		if (this.distance <= distance) {
			throw new IllegalArgumentException("기존 역 사이 길이보다 신규 구간의 길이가 크거나 같습니다");
		}
		this.distance -= distance;
	}
}
