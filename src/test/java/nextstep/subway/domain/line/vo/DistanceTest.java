package nextstep.subway.domain.line.vo;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("거리 테스트")
class DistanceTest {

	@Test
	@DisplayName("거리 생성 검증")
	void createDistance() {
		// When
		Distance 생성된_거리 = new Distance(10);

		// Then
		assertThat(생성된_거리).isNotNull();
	}

	@ParameterizedTest
	@ValueSource(ints = {0, -1})
	@DisplayName("거리의 값이 0 이하 일 경우 예외 발생")
	void createDistanceException(int value) {

		assertThatThrownBy(() -> new Distance(value))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("거리는 0보다 커야 합니다.");
	}


}