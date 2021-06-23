package nextstep.subway.common.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("간격 테스트")
public class DistanceTest {

	private static final int ZERO = 0;
	private static final int ONE = 1;

	@Test
	void 생성() {
		//given
		int 거리 = ONE;

		//when
		Distance 간격 = Distance.generate(거리);

		//then
		assertThat(간격).isNotNull();
	}

	@Test
	void 생성_0이하_예외발생() {
		//given
		int 거리 = ZERO;

		//when

		//then
		assertThatThrownBy(() -> Distance.generate(거리)).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void 동일성() {
		//given
		Distance 간격 = Distance.generate(ONE);
		Distance 비교할_간격 = Distance.generate(ONE);

		//when
		boolean 동일성여부 = 간격.equals(비교할_간격);

		//then
		assertThat(동일성여부).isTrue();
	}
}
