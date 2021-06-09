package nextstep.subway.line.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DistanceTest {

	@Test
	@DisplayName("구간의 거리는 양수여야 합니다.")
	void validatePositiveTest() {
		assertThatThrownBy(() -> Distance.valueOf(-1))
			.isInstanceOf(IllegalArgumentException.class);
	}

}