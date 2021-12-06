package nextstep.subway.line;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.exception.AppException;
import nextstep.subway.line.domain.Distance;

public class DistanceTest {

	@Test
	@DisplayName("생성 테스트")
	public void createTest() {
		// given
		int given = 10;

		// when
		Distance distance = Distance.of(given);

		// then
		assertThat(distance).isEqualTo(Distance.of(given));
	}

	@Test
	@DisplayName("두 거리의 차를 구한다")
	public void minusTest() {
		// given
		Distance distance1 = Distance.of(10);
		Distance distance2 = Distance.of(9);

		// when
		Distance different = distance1.minus(distance2);

		// then
		assertThat(different).isEqualTo(Distance.of(1));
	}

	@Test
	@DisplayName("0보다 커야한다")
	public void validateTest() {
		// given
		int given = 0;

		// when, then
		assertThatThrownBy(() -> Distance.of(given))
			.isInstanceOf(AppException.class);
	}
}
