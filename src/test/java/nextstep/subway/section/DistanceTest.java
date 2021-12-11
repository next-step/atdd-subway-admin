package nextstep.subway.section;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.common.exception.BadParameterException;
import nextstep.subway.line.domain.Distance;

public class DistanceTest {
	@Test
	@DisplayName("minus 성공")
	void minus_success() {
		Distance distance = new Distance(5);
		distance.minus(new Distance(2));

		assertThat(distance).isEqualTo(new Distance(3));
	}

	@Test
	@DisplayName("minus 할 때 기존 값과 같거나 큰 수를 빼면 예외")
	void minus_greaterThanOrEqualValue_exception() {
		Distance distance = new Distance(5);

		assertThatThrownBy(() -> {
			distance.minus(new Distance(5));
		}).isInstanceOf(BadParameterException.class)
			.hasMessage("추가하려는 구간의 길이가 기존에 존재하는 길이와 같거나 깁니다.");

		assertThatThrownBy(() -> {
			distance.minus(new Distance(10));
		}).isInstanceOf(BadParameterException.class)
			.hasMessage("추가하려는 구간의 길이가 기존에 존재하는 길이와 같거나 깁니다.");
	}
}
