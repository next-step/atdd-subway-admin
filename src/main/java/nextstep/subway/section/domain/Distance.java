package nextstep.subway.section.domain;

import javax.persistence.Embeddable;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Embeddable
@Getter
public class Distance {
	private static final int MINIMUM_DISTANCE = 1;

	private Integer distance;

	public Distance(Integer distance) {
		validateMinimumDistance(distance);
		this.distance = distance;
	}

	public Distance(String distance) {
		int distanceInt = Integer.parseInt(distance);
		validateMinimumDistance(distanceInt);
		this.distance = distanceInt;
	}

	public Distance calculateDistance(Distance distance) {
		validateDistance(distance);
		return null;
	}

	private void validateMinimumDistance(int distance) {
		if (MINIMUM_DISTANCE > distance) {
			throw new IllegalArgumentException("거리는 1 이상만 가능합니다.");
		}
	}

	private void validateDistance(Distance distance) {
		if (distance.distance >= this.distance) {
			throw new IllegalArgumentException("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없습니다.");
		}
	}
}
