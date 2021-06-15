package nextstep.subway.section.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.exception.SubwayLogicException;

class DistanceTest {

	@Test
	@DisplayName("distance 정상 생성, 실패 로직 테스트")
	void testDistance() {

		Distance distance = new Distance(1);
		Assertions.assertThat(distance).isEqualTo(new Distance(1));

		Assertions.assertThatThrownBy(() -> {
			new Distance(0);
		}).isInstanceOf(SubwayLogicException.class)
			.hasMessageContaining("distance는 0이상 이어야 합니다.");
	}
}